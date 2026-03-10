import os
import sys
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

try:
    response = requests.get(url, params=params)
    response.raise_for_status()
except requests.HTTPError as exc:
    sys.exit(f"Error fetching odds from {url}: {exc}")

data = response.json()

for game in data:
    home = game.get("home_team", "Unknown")
    away = game.get("away_team", "Unknown")
    print(home, "vs", away)

    for bookmaker in game.get("bookmakers", []):
        print("  Casa:", bookmaker.get("title", "Unknown"))

        for market in bookmaker.get("markets", []):
            for outcome in market.get("outcomes", []):
                print("   ", outcome.get("name"), outcome.get("price"))

    print()
