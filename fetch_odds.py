import csv
import os
import sys
from datetime import datetime, timezone

import requests

BASE_URL = "https://api.the-odds-api.com/v4/sports/{sport}/odds"


def build_url(sport: str = "basketball_nba") -> str:
    return BASE_URL.format(sport=sport)


def build_params(api_key: str, regions: str = "eu", markets: str = "h2h") -> dict:
    return {
        "apiKey": api_key,
        "regions": regions,
        "markets": markets,
    }


def fetch_odds(api_key: str, sport: str = "basketball_nba") -> list:
    url = build_url(sport)
    params = build_params(api_key)
    response = requests.get(url, params=params)
    response.raise_for_status()
    return response.json()


def parse_odds(data: list) -> list:
    rows = []
    for game in data:
        home = game.get("home_team", "Unknown")
        away = game.get("away_team", "Unknown")
        for bookmaker in game.get("bookmakers", []):
            title = bookmaker.get("title", "Unknown")
            for market in bookmaker.get("markets", []):
                for outcome in market.get("outcomes", []):
                    rows.append({
                        "home_team": home,
                        "away_team": away,
                        "bookmaker": title,
                        "outcome": outcome.get("name"),
                        "price": outcome.get("price"),
                    })
    return rows


def write_csv(rows: list, filename: str) -> None:
    with open(filename, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["home_team", "away_team", "bookmaker", "outcome", "price"])
        for row in rows:
            writer.writerow([
                row["home_team"],
                row["away_team"],
                row["bookmaker"],
                row["outcome"],
                row["price"],
            ])


def generate_filename() -> str:
    return f"odds_{datetime.now(timezone.utc).strftime('%Y-%m-%d_%H-%M-%S')}.csv"


def main() -> None:
    api_key = os.environ.get("ODDS_API_KEY")
    if not api_key:
        sys.exit("Error: the ODDS_API_KEY environment variable is not set.")

    url = build_url()
    try:
        data = fetch_odds(api_key)
    except requests.HTTPError as exc:
        sys.exit(f"Error fetching odds from {url}: {exc}")

    rows = parse_odds(data)

    for game_rows in _group_by_game(rows):
        if not game_rows:
            continue
        home = game_rows[0]["home_team"]
        away = game_rows[0]["away_team"]
        print(home, "vs", away)
        for row in game_rows:
            print("  Casa:", row["bookmaker"])
            print("   ", row["outcome"], row["price"])
        print()

    csv_filename = generate_filename()
    try:
        write_csv(rows, csv_filename)
    except OSError as exc:
        sys.exit(f"Error creating CSV file {csv_filename}: {exc}")

    print(f"Results exported to {csv_filename}")


def _group_by_game(rows: list) -> list:
    seen = {}
    order = []
    for row in rows:
        key = (row["home_team"], row["away_team"])
        if key not in seen:
            seen[key] = []
            order.append(key)
        seen[key].append(row)
    return [seen[k] for k in order]


if __name__ == "__main__":
    main()
