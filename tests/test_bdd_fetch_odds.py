"""Cucumber/BDD tests for fetch_odds — links scenarios to step definitions."""
# This file is intentionally minimal: all step definitions and scenario
# bindings live in steps/fetch_odds_steps.py which is imported here so
# pytest-bdd can collect and run them as regular test items.
from tests.features.steps.fetch_odds_steps import *  # noqa: F401, F403
