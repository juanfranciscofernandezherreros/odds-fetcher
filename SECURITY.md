# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please **do not** open a public issue.

Instead, report it privately by opening a [GitHub Security Advisory](https://github.com/juanfranciscofernandezherreros/cuddly-octo-computing-machine/security/advisories/new).

Please include:
- A description of the vulnerability and its potential impact
- Steps to reproduce the issue
- Any relevant logs, stack traces, or proof-of-concept code

You can expect an acknowledgement within **48 hours** and a resolution or status update within **7 days**.

## Security Considerations

- The application requires an `ODDS_API_KEY` environment variable. **Never commit your API key to source control.**
- When running with Docker, pass secrets via environment variables or Docker secrets — never bake them into images.
- The application makes outbound HTTPS requests to `https://api.the-odds-api.com`. Ensure your network security policies allow this.
- CSV output files may contain publicly available sports betting odds and do not contain personally identifiable information (PII).
