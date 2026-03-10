"""Unit tests for fetch_odds module."""
import csv
import os

import pytest
import requests

from fetch_odds import (
    build_params,
    build_url,
    fetch_odds,
    generate_filename,
    parse_odds,
    write_csv,
)

# ---------------------------------------------------------------------------
# Sample fixture data
# ---------------------------------------------------------------------------

SAMPLE_DATA = [
    {
        "home_team": "Los Angeles Lakers",
        "away_team": "Golden State Warriors",
        "bookmakers": [
            {
                "title": "Bet365",
                "markets": [
                    {
                        "outcomes": [
                            {"name": "Los Angeles Lakers", "price": 1.80},
                            {"name": "Golden State Warriors", "price": 2.10},
                        ]
                    }
                ],
            }
        ],
    }
]


# ---------------------------------------------------------------------------
# build_url
# ---------------------------------------------------------------------------

class TestBuildUrl:
    def test_default_sport(self):
        assert build_url() == "https://api.the-odds-api.com/v4/sports/basketball_nba/odds"

    def test_custom_sport(self):
        assert build_url("basketball_wnba") == (
            "https://api.the-odds-api.com/v4/sports/basketball_wnba/odds"
        )


# ---------------------------------------------------------------------------
# build_params
# ---------------------------------------------------------------------------

class TestBuildParams:
    def test_default_params(self):
        params = build_params("MY_KEY")
        assert params == {"apiKey": "MY_KEY", "regions": "eu", "markets": "h2h"}

    def test_custom_params(self):
        params = build_params("KEY", regions="us", markets="spreads")
        assert params["regions"] == "us"
        assert params["markets"] == "spreads"
        assert params["apiKey"] == "KEY"


# ---------------------------------------------------------------------------
# fetch_odds
# ---------------------------------------------------------------------------

class TestFetchOdds:
    def test_returns_json_on_success(self, mocker):
        mock_response = mocker.MagicMock()
        mock_response.json.return_value = SAMPLE_DATA
        mocker.patch("fetch_odds.requests.get", return_value=mock_response)

        result = fetch_odds("FAKE_KEY")

        assert result == SAMPLE_DATA

    def test_raises_on_http_error(self, mocker):
        mock_response = mocker.MagicMock()
        mock_response.raise_for_status.side_effect = requests.HTTPError("403 Forbidden")
        mocker.patch("fetch_odds.requests.get", return_value=mock_response)

        with pytest.raises(requests.HTTPError):
            fetch_odds("BAD_KEY")

    def test_uses_correct_url_and_params(self, mocker):
        mock_get = mocker.patch("fetch_odds.requests.get")
        mock_get.return_value.json.return_value = []

        fetch_odds("MY_KEY", sport="basketball_nba")

        mock_get.assert_called_once_with(
            "https://api.the-odds-api.com/v4/sports/basketball_nba/odds",
            params={"apiKey": "MY_KEY", "regions": "eu", "markets": "h2h"},
        )


# ---------------------------------------------------------------------------
# parse_odds
# ---------------------------------------------------------------------------

class TestParseOdds:
    def test_parses_single_game(self):
        rows = parse_odds(SAMPLE_DATA)

        assert len(rows) == 2
        assert rows[0] == {
            "home_team": "Los Angeles Lakers",
            "away_team": "Golden State Warriors",
            "bookmaker": "Bet365",
            "outcome": "Los Angeles Lakers",
            "price": 1.80,
        }
        assert rows[1]["outcome"] == "Golden State Warriors"
        assert rows[1]["price"] == 2.10

    def test_empty_data_returns_empty_list(self):
        assert parse_odds([]) == []

    def test_game_without_bookmakers(self):
        data = [{"home_team": "Team A", "away_team": "Team B", "bookmakers": []}]
        assert parse_odds(data) == []

    def test_missing_home_away_defaults_to_unknown(self):
        data = [
            {
                "bookmakers": [
                    {
                        "title": "Betfair",
                        "markets": [
                            {"outcomes": [{"name": "Team A", "price": 1.5}]}
                        ],
                    }
                ]
            }
        ]
        rows = parse_odds(data)
        assert rows[0]["home_team"] == "Unknown"
        assert rows[0]["away_team"] == "Unknown"

    def test_multiple_bookmakers(self):
        data = [
            {
                "home_team": "Team A",
                "away_team": "Team B",
                "bookmakers": [
                    {
                        "title": "Book1",
                        "markets": [
                            {"outcomes": [{"name": "Team A", "price": 1.5}]}
                        ],
                    },
                    {
                        "title": "Book2",
                        "markets": [
                            {"outcomes": [{"name": "Team B", "price": 2.5}]}
                        ],
                    },
                ],
            }
        ]
        rows = parse_odds(data)
        assert len(rows) == 2
        bookmakers = {r["bookmaker"] for r in rows}
        assert bookmakers == {"Book1", "Book2"}


# ---------------------------------------------------------------------------
# write_csv
# ---------------------------------------------------------------------------

class TestWriteCsv:
    def test_writes_header_and_rows(self, tmp_path):
        rows = [
            {
                "home_team": "Lakers",
                "away_team": "Warriors",
                "bookmaker": "Bet365",
                "outcome": "Lakers",
                "price": 1.80,
            }
        ]
        filename = str(tmp_path / "test_odds.csv")
        write_csv(rows, filename)

        with open(filename, encoding="utf-8") as f:
            reader = csv.reader(f)
            header = next(reader)
            data_row = next(reader)

        assert header == ["home_team", "away_team", "bookmaker", "outcome", "price"]
        assert data_row == ["Lakers", "Warriors", "Bet365", "Lakers", "1.8"]

    def test_writes_empty_rows(self, tmp_path):
        filename = str(tmp_path / "empty.csv")
        write_csv([], filename)

        with open(filename, encoding="utf-8") as f:
            reader = csv.reader(f)
            header = next(reader)
            rows = list(reader)

        assert header == ["home_team", "away_team", "bookmaker", "outcome", "price"]
        assert rows == []

    def test_multiple_rows(self, tmp_path):
        rows = parse_odds(SAMPLE_DATA)
        filename = str(tmp_path / "multi.csv")
        write_csv(rows, filename)

        with open(filename, encoding="utf-8") as f:
            reader = csv.reader(f)
            lines = list(reader)

        assert len(lines) == 3  # header + 2 data rows


# ---------------------------------------------------------------------------
# generate_filename
# ---------------------------------------------------------------------------

class TestGenerateFilename:
    def test_filename_starts_with_odds(self):
        assert generate_filename().startswith("odds_")

    def test_filename_ends_with_csv(self):
        assert generate_filename().endswith(".csv")

    def test_filename_contains_timestamp(self):
        name = generate_filename()
        # Format: odds_YYYY-MM-DD_HH-MM-SS.csv
        parts = name.replace("odds_", "").replace(".csv", "").split("_")
        assert len(parts) == 2
        assert len(parts[0]) == 10  # YYYY-MM-DD
        assert len(parts[1]) == 8   # HH-MM-SS
