package com.odds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bookmaker {

    private String title;
    private List<Market> markets;

    public Bookmaker() {
    }

    public Bookmaker(String title, List<Market> markets) {
        this.title = title;
        this.markets = markets;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Market> getMarkets() {
        return markets != null ? markets : Collections.emptyList();
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }
}
