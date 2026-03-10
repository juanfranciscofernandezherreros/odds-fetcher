import csv
import os
import sys
from datetime import datetime, timezone

import requests

API_KEY = os.environ.get("ODDS_API_KEY")
if not API_KEY:
    sys.exit("Error: the ODDS_API_KEY environment variable is not set.")

sport = "basketball_nba"

url = f"https://api.the-odds-api.com/v4/sports/{sport}/odds"

params = {
    "apiKey": API_KEY,
    "regions": "eu",
    "markets": "h2h"
}

csv_filename = f"odds_{datetime.now(timezone.utc).strftime('%Y-%m-%d_%H-%M-%S')}.csv"

try:
    response = requests.get(url, params=params)
    response.raise_for_status()
except requests.HTTPError as exc:
    sys.exit(f"Error fetching odds from {url}: {exc}")

data = response.json()

try:
    csvfile = open(csv_filename, "w", newline="", encoding="utf-8")
except OSError as exc:
    sys.exit(f"Error creating CSV file {csv_filename}: {exc}")

with csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(["home_team", "away_team", "bookmaker", "outcome", "price"])

    for game in data:
        home = game.get("home_team", "Unknown")
        away = game.get("away_team", "Unknown")
        print(home, "vs", away)

        for bookmaker in game.get("bookmakers", []):
            print("  Casa:", bookmaker.get("title", "Unknown"))

            for market in bookmaker.get("markets", []):
                for outcome in market.get("outcomes", []):
                    print("   ", outcome.get("name"), outcome.get("price"))
                    writer.writerow([
                        home,
                        away,
                        bookmaker.get("title", "Unknown"),
                        outcome.get("name"),
                        outcome.get("price"),
                    ])

        print()

print(f"Results exported to {csv_filename}")
