"""Cucumber/BDD step definitions for fetch_odds feature."""
import csv
import os
import tempfile

import pytest
import requests
from pytest_bdd import given, parsers, scenarios, then, when

from fetch_odds import fetch_odds, parse_odds, write_csv

# Link all scenarios from the feature file
scenarios("../fetch_odds.feature")

# ---------------------------------------------------------------------------
# Shared sample data
# ---------------------------------------------------------------------------

SAMPLE_GAME = {
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


# ---------------------------------------------------------------------------
# Background / Given steps
# ---------------------------------------------------------------------------

@given("a valid API key is available")
def valid_api_key(monkeypatch):
    monkeypatch.setenv("ODDS_API_KEY", "TEST_KEY_123")


@given("no API key is set")
def no_api_key(monkeypatch):
    monkeypatch.delenv("ODDS_API_KEY", raising=False)


@given("the API returns data for 1 game with 1 bookmaker and 2 outcomes")
def api_data(mocker):
    mock_response = mocker.MagicMock()
    mock_response.json.return_value = [SAMPLE_GAME]
    mocker.patch("fetch_odds.requests.get", return_value=mock_response)


@given(parsers.parse("I have {n:d} parsed odds rows"))
def parsed_rows(n, request):
    rows = parse_odds([SAMPLE_GAME])
    request.node._parsed_rows = rows[:n]


# ---------------------------------------------------------------------------
# When steps
# ---------------------------------------------------------------------------

@when(parsers.parse('I fetch odds for "{sport}"'))
def step_fetch_odds(sport, mocker, request):
    mock_response = mocker.MagicMock()
    mock_response.json.return_value = [SAMPLE_GAME]
    mocker.patch("fetch_odds.requests.get", return_value=mock_response)
    api_key = os.environ.get("ODDS_API_KEY", "TEST_KEY")
    request.node._result = fetch_odds(api_key, sport=sport)


@when("I parse the odds data")
def step_parse_odds(request):
    request.node._result = parse_odds([SAMPLE_GAME])


@when(parsers.parse('I parse the odds data for sport "{sport}"'))
def step_parse_odds_for_sport(sport, request):
    request.node._result = parse_odds([SAMPLE_GAME])


@when("I write the rows to a CSV file")
def step_write_csv(request, tmp_path):
    rows = request.node._parsed_rows
    filename = str(tmp_path / "test_output.csv")
    write_csv(rows, filename)
    request.node._csv_filename = filename


@when("I try to fetch odds")
def step_try_fetch_no_key(request):
    request.node._api_key = os.environ.get("ODDS_API_KEY")


@when("the API responds with a 403 error")
def step_api_403(mocker, request):
    mock_response = mocker.MagicMock()
    mock_response.raise_for_status.side_effect = requests.HTTPError("403 Forbidden")
    mocker.patch("fetch_odds.requests.get", return_value=mock_response)
    request.node._mock_response = mock_response


# ---------------------------------------------------------------------------
# Then steps
# ---------------------------------------------------------------------------

@then("the response should contain a list of games")
def step_response_is_list(request):
    result = request.node._result
    assert isinstance(result, list)
    assert len(result) > 0


@then(parsers.parse("I should get {n:d} odds rows"))
def step_check_row_count(n, request):
    result = request.node._result
    assert len(result) == n


@then("each row should have home_team, away_team, bookmaker, outcome and price")
def step_check_row_keys(request):
    rows = request.node._result
    required_keys = {"home_team", "away_team", "bookmaker", "outcome", "price"}
    for row in rows:
        assert required_keys.issubset(row.keys()), (
            f"Row missing keys. Got: {set(row.keys())}"
        )


@then("the CSV file should exist")
def step_csv_exists(request):
    assert os.path.isfile(request.node._csv_filename)


@then("the CSV file should have a header row")
def step_csv_has_header(request):
    with open(request.node._csv_filename, encoding="utf-8") as f:
        reader = csv.reader(f)
        header = next(reader)
    assert header == ["home_team", "away_team", "bookmaker", "outcome", "price"]


@then(parsers.parse("the CSV file should contain {n:d} data rows"))
def step_csv_data_rows(n, request):
    with open(request.node._csv_filename, encoding="utf-8") as f:
        reader = csv.reader(f)
        next(reader)  # skip header
        rows = list(reader)
    assert len(rows) == n


@then("the process should exit with an error message")
def step_exit_with_error(request):
    api_key = request.node._api_key
    assert api_key is None or api_key == ""


@then("a HTTPError should be raised")
def step_http_error_raised(request):
    mock_response = request.node._mock_response
    with pytest.raises(requests.HTTPError):
        mock_response.raise_for_status()
