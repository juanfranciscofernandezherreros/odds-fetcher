package com.odds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Market {

    private String key;
    private List<Outcome> outcomes;

    public Market() {
    }

    public Market(String key, List<Outcome> outcomes) {
        this.key = key;
        this.outcomes = outcomes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Outcome> getOutcomes() {
        return outcomes != null ? outcomes : Collections.emptyList();
    }

    public void setOutcomes(List<Outcome> outcomes) {
        this.outcomes = outcomes;
    }
}
