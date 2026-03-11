# Contributing to NBA Odds Fetcher

Thank you for your interest in contributing! All contributions are welcome and appreciated.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Running Tests](#running-tests)
- [Pull Request Guidelines](#pull-request-guidelines)
- [Reporting Bugs](#reporting-bugs)
- [Requesting Features](#requesting-features)

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## How to Contribute

1. **Fork** the repository on GitHub.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/your-username/cuddly-octo-computing-machine.git
   cd cuddly-octo-computing-machine
   ```
3. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Make your changes** and write or update tests as needed.
5. **Run the test suite** to make sure everything passes.
6. **Commit** your changes with a clear, descriptive message.
7. **Push** to your fork and open a Pull Request.

## Development Setup

**Requirements:**
- Java 17+
- Maven 3.9+
- An `ODDS_API_KEY` from [The Odds API](https://the-odds-api.com/) (only needed for running the application; not for tests)

**Build:**
```bash
mvn clean package
```

**Run locally:**
```bash
ODDS_API_KEY=your_key java -jar target/fetch-odds-1.0.0.jar
```

**Run with Docker:**
```bash
ODDS_API_KEY=your_key docker compose up --build
```

## Running Tests

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest=CsvWriterTest,OddsParserTest,OddsFetcherTest

# Run only the benchmark test
mvn test -Dtest=CsvWriterBenchmarkTest
```

All tests must pass before a pull request will be merged.

## Pull Request Guidelines

- Keep PRs focused — one feature or fix per PR.
- Include tests for any new functionality.
- Update documentation (`README.md`, `docs/`) if your change affects usage or behaviour.
- Ensure the CI workflow passes on your branch before requesting a review.
- Reference any related issues in the PR description (e.g. `Closes #42`).

## Reporting Bugs

Please use the [Bug Report](.github/ISSUE_TEMPLATE/bug_report.md) issue template. Include:
- Steps to reproduce the issue
- Expected vs. actual behaviour
- Java version, OS, and relevant environment details

## Requesting Features

Please use the [Feature Request](.github/ISSUE_TEMPLATE/feature_request.md) issue template. Describe the problem you are trying to solve and the proposed solution.
