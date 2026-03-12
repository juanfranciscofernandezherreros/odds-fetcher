# NBA Odds Fetcher

[![Run Tests](https://github.com/juanfranciscofernandezherreros/odds-fetcher/actions/workflows/run_tests.yml/badge.svg)](https://github.com/juanfranciscofernandezherreros/odds-fetcher/actions/workflows/run_tests.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17](https://img.shields.io/badge/Java-17-blue?logo=openjdk)](https://adoptium.net/)
[![Spring Boot 3.2.5](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)

A Spring Boot application that fetches NBA sports betting odds from [The Odds API](https://the-odds-api.com/) and exports them to a CSV file.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [Docker](#docker)
- [Tests](#tests)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Performance](#performance)
- [Contributing](#contributing)
- [License](#license)

## Features

- Fetches live NBA head-to-head odds from multiple bookmakers via [The Odds API](https://the-odds-api.com/)
- Exports odds data to a timestamped CSV file
- Optimized CSV writing using `BufferedWriter` with a 64 KB buffer (16–33% faster than alternatives)
- Scheduled daily runs via GitHub Actions
- Multi-stage Docker build for minimal image size

## Requirements

- Java 17+
- Maven 3.9+
- `ODDS_API_KEY` environment variable — obtain a free key from [The Odds API](https://the-odds-api.com/)

## Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/juanfranciscofernandezherreros/odds-fetcher.git
cd odds-fetcher

# 2. Build
mvn clean package

# 3. Run
ODDS_API_KEY=your_key java -jar target/fetch-odds-1.0.0.jar
```

The application writes a CSV file named `odds_<timestamp>.csv` in the current directory.

### CSV Output Format

| Column       | Description                          |
|--------------|--------------------------------------|
| `game_id`    | Unique game identifier               |
| `commence`   | Game start time (ISO-8601)           |
| `home_team`  | Home team name                       |
| `away_team`  | Away team name                       |
| `bookmaker`  | Bookmaker name                       |
| `outcome`    | Outcome label (e.g. team name)       |
| `price`      | Decimal odds price                   |

## Docker

```bash
ODDS_API_KEY=your_key docker compose up --build
```

Output CSV files are written to the `./output` directory on the host.

## Tests

```bash
# Run all tests (13 unit tests + 1 benchmark)
mvn test

# Run only unit tests
mvn test -Dtest=CsvWriterTest,OddsParserTest,OddsFetcherTest

# Run only the benchmark
mvn test -Dtest=CsvWriterBenchmarkTest
```

## Project Structure

```
src/main/java/com/odds/
├── OddsApplication.java        # Spring Boot entry point
├── config/AppConfig.java       # RestTemplate bean
├── model/
│   ├── Game.java               # API response: game with teams
│   ├── Bookmaker.java          # API response: bookmaker
│   ├── Market.java             # API response: market (h2h, etc.)
│   ├── Outcome.java            # API response: outcome + price
│   └── OddsRow.java            # Flat row for CSV export
├── service/
│   ├── OddsFetcher.java        # Fetches odds from The Odds API
│   ├── OddsParser.java         # Flattens nested JSON into rows
│   └── CsvWriter.java          # Optimized CSV file writer
└── runner/OddsRunner.java      # CommandLineRunner orchestrator
```

## Configuration

| Property                    | Default                                          | Description              |
|-----------------------------|--------------------------------------------------|--------------------------|
| `ODDS_API_KEY`              | *(required)*                                     | API key for The Odds API |
| `odds.api.base-url`         | `https://api.the-odds-api.com/v4/sports`         | Base URL for the API     |
| `odds.api.sport`            | `basketball_nba`                                 | Sport to fetch odds for  |

Configuration is managed in `src/main/resources/application.properties`.

## Architecture

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the full architecture documentation including data flow, component, package, and sequence diagrams.

## Performance

CSV writing is optimized using `BufferedWriter` with a 64 KB buffer and a single `write()` call per row.
This strategy consistently outperforms alternatives by **16–33%** in standalone benchmarks.

See [docs/PERFORMANCE.md](docs/PERFORMANCE.md) for full benchmark results and analysis.

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) to get started.

## License

This project is licensed under the [MIT License](LICENSE).
