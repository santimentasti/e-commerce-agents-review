"""PR architecture reviewer using the Google Gemini API."""

import json
import os
import sys

import google.generativeai as genai
import requests

GITHUB_TOKEN = os.environ["GITHUB_TOKEN"]
GEMINI_API_KEY = os.environ["GEMINI_API_KEY"]
PR_NUMBER = os.environ["PR_NUMBER"]
REPO = os.environ["REPO"]
COVERAGE = os.environ.get("COVERAGE", "unknown")

GH_HEADERS = {
    "Authorization": f"Bearer {GITHUB_TOKEN}",
    "Accept": "application/vnd.github+json",
    "X-GitHub-Api-Version": "2022-11-28",
}

SYSTEM_PROMPT = """
You are a strict Java architect reviewer. Analyze the PR diff and report violations
of the following rules. For each violation, output: file, line range, rule broken,
and a concrete fix suggestion.

SOLID rules to enforce:
- SRP: No class should handle more than one responsibility. Controllers must not
  contain business logic. Services must not contain persistence logic.
- OCP: Business rules must be extensible via interfaces, not by modifying existing
  classes. Flag any switch/if-else on type/status that should be a strategy pattern.
- LSP: Subtypes must be substitutable for their base types. Flag overrides that
  weaken preconditions or strengthen postconditions.
- ISP: No interface should force implementors to leave methods empty or throw
  UnsupportedOperationException. Flag fat interfaces.
- DIP: High-level modules (services) must depend on abstractions (port interfaces),
  never on concrete infrastructure classes (JpaRepository, EntityManager, etc.)

Architecture rules:
- Domain classes must have zero imports from org.springframework or javax.persistence
- Services in application/ must not import from infrastructure/ or api/
- Controllers must not import from infrastructure/ or domain directly
  (only through use case interfaces and DTOs)
- DTOs must never be used inside domain or application layers
- No business logic in constructors of JPA @Entity classes

Test coverage rules:
- Every public method in application/service/ must have at least one unit test
- If a new service method is added without a corresponding test class change, flag it
- Coverage must not drop below 80% overall

Output format: structured JSON with this shape:
{
  "summary": "string",
  "violations": [
    {
      "file": "string",
      "lines": "string",
      "rule": "SRP|OCP|LSP|ISP|DIP|ARCH|COVERAGE",
      "severity": "error|warning",
      "description": "string",
      "suggestion": "string"
    }
  ],
  "coverage_delta": "string",
  "approved": boolean
}
"""


def fetch_pr_diff() -> str:
    url = f"https://api.github.com/repos/{REPO}/pulls/{PR_NUMBER}"
    resp = requests.get(url, headers={**GH_HEADERS, "Accept": "application/vnd.github.v3.diff"})
    resp.raise_for_status()
    return resp.text


def fetch_changed_java_files() -> list[str]:
    url = f"https://api.github.com/repos/{REPO}/pulls/{PR_NUMBER}/files"
    resp = requests.get(url, headers=GH_HEADERS)
    resp.raise_for_status()
    files = resp.json()
    return [f["filename"] for f in files if f["filename"].endswith(".java")]


def call_gemini(diff: str, java_files: list[str]) -> dict:
    genai.configure(api_key=GEMINI_API_KEY)
    model = genai.GenerativeModel(
        model_name="gemini-2.0-flash",
        system_instruction=SYSTEM_PROMPT,
    )

    user_content = (
        f"PR #{PR_NUMBER} in {REPO}\n\n"
        f"Current test coverage: {COVERAGE}%\n\n"
        f"Changed Java files:\n"
        + "\n".join(f"  - {f}" for f in java_files)
        + "\n\n"
        f"<diff>\n{diff[:80000]}\n</diff>\n\n"
        "Analyze the diff and return ONLY the JSON object described in the system prompt."
    )

    response = model.generate_content(user_content)
    raw = response.text.strip()

    if raw.startswith("```"):
        raw = raw.split("```")[1]
        if raw.startswith("json"):
            raw = raw[4:]
    return json.loads(raw)


def violations_to_markdown(review: dict) -> str:
    lines = ["## AI Architecture Review\n"]
    lines.append(f"**Summary:** {review['summary']}\n")
    lines.append(f"**Coverage delta:** {review.get('coverage_delta', 'N/A')}\n")
    approved_label = "✅ Approved" if review["approved"] else "❌ Not Approved"
    lines.append(f"**Status:** {approved_label}\n")

    violations = review.get("violations", [])
    if not violations:
        lines.append("\nNo violations found.")
        return "\n".join(lines)

    lines.append("\n### Violations\n")
    lines.append("| File | Lines | Rule | Severity | Description | Suggestion |")
    lines.append("|------|-------|------|----------|-------------|------------|")

    for v in violations:
        file_ = v.get("file", "")
        lines_ = v.get("lines", "")
        rule = v.get("rule", "")
        sev = v.get("severity", "")
        sev_icon = "🔴" if sev == "error" else "🟡"
        desc = v.get("description", "").replace("|", "\\|")
        suggestion = v.get("suggestion", "").replace("|", "\\|")
        lines.append(f"| `{file_}` | {lines_} | `{rule}` | {sev_icon} {sev} | {desc} | {suggestion} |")

    return "\n".join(lines)


def post_pr_comment(body: str) -> None:
    url = f"https://api.github.com/repos/{REPO}/issues/{PR_NUMBER}/comments"
    resp = requests.post(url, headers=GH_HEADERS, json={"body": body})
    resp.raise_for_status()


def main() -> int:
    print(f"Fetching diff for PR #{PR_NUMBER} in {REPO}...")
    diff = fetch_pr_diff()
    java_files = fetch_changed_java_files()
    print(f"Changed Java files: {len(java_files)}")

    print("Calling Gemini API...")
    review = call_gemini(diff, java_files)

    print("Review result:")
    print(json.dumps(review, indent=2))

    comment = violations_to_markdown(review)
    print("Posting PR comment...")
    post_pr_comment(comment)

    errors = [v for v in review.get("violations", []) if v.get("severity") == "error"]
    if not review.get("approved") or errors:
        print(f"Review FAILED: approved={review.get('approved')}, errors={len(errors)}")
        return 1

    print("Review PASSED.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
