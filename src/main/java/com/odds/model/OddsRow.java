package com.odds.model;

public class OddsRow {

    private final String homeTeam;
    private final String awayTeam;
    private final String bookmaker;
    private final String outcome;
    private final Double price;

    public OddsRow(String homeTeam, String awayTeam, String bookmaker, String outcome, Double price) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.bookmaker = bookmaker;
        this.outcome = outcome;
        this.price = price;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getBookmaker() {
        return bookmaker;
    }

    public String getOutcome() {
        return outcome;
    }

    public Double getPrice() {
        return price;
    }
}
