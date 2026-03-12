# Architecture

## Overview

NBA Odds Fetcher is a Spring Boot CLI application that fetches live NBA betting odds
from [The Odds API](https://the-odds-api.com/) and exports them to a timestamped CSV file.

The application follows a linear data pipeline: **fetch → parse → write**.

---

## Data Flow

```mermaid
flowchart LR
    A["The Odds API\n(REST/JSON)"] -->|HTTP GET| B["OddsFetcher\n(RestTemplate)"]
    B -->|"List&lt;Game&gt;"| C["OddsParser"]
    C -->|"List&lt;OddsRow&gt;"| D["CsvWriter\n(BufferedWriter 64 KB)"]
    D --> E["odds_&lt;timestamp&gt;.csv"]
```

---

## Component Diagram

```mermaid
classDiagram
    direction TB

    class OddsApplication {
        +main(String[] args)
    }

    class OddsRunner {
        -OddsFetcher fetcher
        -OddsParser parser
        -CsvWriter writer
        +run(String... args)
    }

    class OddsFetcher {
        -RestTemplate restTemplate
        -String baseUrl
        -String sport
        +fetch(String apiKey) List~Game~
    }

    class OddsParser {
        +parse(List~Game~) List~OddsRow~
    }

    class CsvWriter {
        +write(List~OddsRow~, String filename)
        -escapeCsv(String field) String
    }

    class AppConfig {
        +restTemplate() RestTemplate
    }

    class Game {
        +String id
        +String commence_time
        +String home_team
        +String away_team
        +List~Bookmaker~ bookmakers
    }

    class Bookmaker {
        +String key
        +String title
        +List~Market~ markets
    }

    class Market {
        +String key
        +List~Outcome~ outcomes
    }

    class Outcome {
        +String name
        +double price
    }

    class OddsRow {
        +String gameId
        +String commence
        +String homeTeam
        +String awayTeam
        +String bookmaker
        +String outcome
        +double price
    }

    OddsApplication --> OddsRunner
    OddsRunner --> OddsFetcher
    OddsRunner --> OddsParser
    OddsRunner --> CsvWriter
    OddsFetcher --> Game
    Game --> Bookmaker
    Bookmaker --> Market
    Market --> Outcome
    OddsParser --> OddsRow
    CsvWriter --> OddsRow
    AppConfig --> OddsFetcher : provides RestTemplate
```

---

## Package Structure

```mermaid
graph TD
    subgraph "com.odds"
        A[OddsApplication]
        subgraph config
            B[AppConfig]
        end
        subgraph runner
            C[OddsRunner]
        end
        subgraph service
            D[OddsFetcher]
            E[OddsParser]
            F[CsvWriter]
        end
        subgraph model
            G[Game]
            H[Bookmaker]
            I[Market]
            J[Outcome]
            K[OddsRow]
        end
    end
```

---

## Sequence Diagram

```mermaid
sequenceDiagram
    participant Main as OddsApplication
    participant Runner as OddsRunner
    participant Fetcher as OddsFetcher
    participant API as The Odds API
    participant Parser as OddsParser
    participant Writer as CsvWriter
    participant File as CSV File

    Main->>Runner: run()
    Runner->>Fetcher: fetch(apiKey)
    Fetcher->>API: GET /v4/sports/basketball_nba/odds
    API-->>Fetcher: JSON (List of Game)
    Fetcher-->>Runner: List<Game>
    Runner->>Parser: parse(games)
    Parser-->>Runner: List<OddsRow>
    Runner->>Writer: write(rows, filename)
    Writer->>File: odds_<timestamp>.csv
```

---

## Technology Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| Language       | Java 17                             |
| Framework      | Spring Boot 3.2.5                   |
| HTTP Client    | RestTemplate (Spring Web)           |
| JSON Parsing   | Jackson (via Spring Boot)           |
| CSV Writing    | BufferedWriter (64 KB buffer)       |
| Testing        | JUnit 5 + Mockito + AssertJ         |
| Build          | Maven 3.9+                          |
| Container      | Multi-stage Docker (Maven + JRE 17) |
| CI/CD          | GitHub Actions                      |

---

## CI/CD Pipelines

| Workflow          | Trigger                            | Description                          |
|-------------------|------------------------------------|--------------------------------------|
| `run_tests.yml`   | Push / Pull Request (all branches) | Runs `mvn test` with Maven caching   |
| `fetch_odds.yml`  | Daily 08:00 UTC / Manual dispatch  | Builds, fetches odds, uploads CSV    |
