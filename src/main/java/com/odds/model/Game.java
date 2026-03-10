package com.odds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    @JsonProperty("home_team")
    private String homeTeam;

    @JsonProperty("away_team")
    private String awayTeam;

    private List<Bookmaker> bookmakers;

    public Game() {
    }

    public Game(String homeTeam, String awayTeam, List<Bookmaker> bookmakers) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.bookmakers = bookmakers;
    }

    public String getHomeTeam() {
        return homeTeam != null ? homeTeam : "Unknown";
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam != null ? awayTeam : "Unknown";
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public List<Bookmaker> getBookmakers() {
        return bookmakers != null ? bookmakers : Collections.emptyList();
    }

    public void setBookmakers(List<Bookmaker> bookmakers) {
        this.bookmakers = bookmakers;
    }
}
