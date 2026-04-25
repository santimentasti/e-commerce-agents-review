package com.ecommerce.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.api.dto.CustomerResponse;
import com.ecommerce.api.mapper.CustomerMapper;
import com.ecommerce.application.port.input.CreateCustomerUseCase;
import com.ecommerce.application.port.input.GetCustomerUseCase;
import com.ecommerce.domain.exception.CustomerNotFoundException;
import com.ecommerce.domain.model.Customer;
import com.ecommerce.domain.model.Email;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CreateCustomerUseCase createCustomerUseCase;

  @MockBean
  private GetCustomerUseCase getCustomerUseCase;

  @MockBean
  private CustomerMapper customerMapper;

  @Test
  void createCustomer_returns201() throws Exception {
    UUID id = UUID.randomUUID();
    Customer customer = new Customer(id, "Alice", new Email("alice@example.com"));
    CustomerResponse response = new CustomerResponse(id, "Alice", "alice@example.com");
    when(createCustomerUseCase.createCustomer(eq("Alice"), eq("alice@example.com")))
        .thenReturn(customer);
    when(customerMapper.toResponse(customer)).thenReturn(response);

    mockMvc.perform(post("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                Map.of("name", "Alice", "email", "alice@example.com"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.email").value("alice@example.com"));
  }

  @Test
  void getCustomer_returns404WhenNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(getCustomerUseCase.getCustomerById(id))
        .thenThrow(new CustomerNotFoundException(id));

    mockMvc.perform(get("/api/v1/customers/" + id))
        .andExpect(status().isNotFound());
  }

  @Test
  void createCustomer_returns400WithInvalidEmail() throws Exception {
    mockMvc.perform(post("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                Map.of("name", "Bob", "email", "not-an-email"))))
        .andExpect(status().isBadRequest());
  }
}
