Feature: NBA Odds Fetcher

  Background:
    Given a valid API key is available

  Scenario: Successfully fetch NBA odds
    When I fetch odds for "basketball_nba"
    Then the response should contain a list of games

  Scenario: Parse odds from API response
    Given the API returns data for 1 game with 1 bookmaker and 2 outcomes
    When I parse the odds data
    Then I should get 2 odds rows
    And each row should have home_team, away_team, bookmaker, outcome and price

  Scenario: Export odds to CSV
    Given I have 2 parsed odds rows
    When I write the rows to a CSV file
    Then the CSV file should exist
    And the CSV file should have a header row
    And the CSV file should contain 2 data rows

  Scenario: Handle missing API key
    Given no API key is set
    When I try to fetch odds
    Then the process should exit with an error message

  Scenario: Handle HTTP error from API
    Given a valid API key is available
    When the API responds with a 403 error
    Then a HTTPError should be raised

  Scenario Outline: Parse odds for multiple sports
    Given the API returns data for 1 game with 1 bookmaker and 2 outcomes
    When I parse the odds data for sport "<sport>"
    Then I should get 2 odds rows

    Examples:
      | sport            |
      | basketball_nba   |
      | basketball_wnba  |
