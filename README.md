# NBA Odds Fetcher

Spring Boot application that fetches NBA sports betting odds from [The Odds API](https://the-odds-api.com/) and exports them to a CSV file.

## Requirements

- Java 17+
- Maven 3.9+
- `ODDS_API_KEY` environment variable

## Build & Run

```bash
mvn clean package
ODDS_API_KEY=your_key java -jar target/fetch-odds-1.0.0.jar
```

## Docker

```bash
ODDS_API_KEY=your_key docker compose up --build
```

## Tests

```bash
mvn test
```
