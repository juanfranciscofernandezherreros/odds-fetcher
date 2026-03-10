package com.odds.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OddsFetcherTest {

    private static final String BASE_URL = "https://api.the-odds-api.com/v4/sports/{sport}/odds";

    @Test
    void buildUrl_defaultSport() {
        OddsFetcher fetcher = new OddsFetcher(null, BASE_URL, "basketball_nba", "eu", "h2h");
        assertEquals("https://api.the-odds-api.com/v4/sports/basketball_nba/odds", fetcher.buildUrl());
    }

    @Test
    void buildUrl_customSport() {
        OddsFetcher fetcher = new OddsFetcher(null, BASE_URL, "basketball_nba", "eu", "h2h");
        assertEquals("https://api.the-odds-api.com/v4/sports/basketball_wnba/odds", fetcher.buildUrl("basketball_wnba"));
    }

    @Test
    void buildUri_containsApiKeyAndParams() {
        OddsFetcher fetcher = new OddsFetcher(null, BASE_URL, "basketball_nba", "eu", "h2h");
        var uri = fetcher.buildUri("test-key", "basketball_nba");
        String uriStr = uri.toString();
        assertTrue(uriStr.contains("apiKey=test-key"));
        assertTrue(uriStr.contains("regions=eu"));
        assertTrue(uriStr.contains("markets=h2h"));
        assertTrue(uriStr.contains("basketball_nba"));
    }
}
