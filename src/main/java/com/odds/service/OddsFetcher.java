package com.odds.service;

import com.odds.model.Game;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class OddsFetcher {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String defaultSport;
    private final String regions;
    private final String markets;

    public OddsFetcher(RestTemplate restTemplate,
                       @Value("${odds.api.base-url}") String baseUrl,
                       @Value("${odds.api.sport}") String defaultSport,
                       @Value("${odds.api.regions}") String regions,
                       @Value("${odds.api.markets}") String markets) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.defaultSport = defaultSport;
        this.regions = regions;
        this.markets = markets;
    }

    public String buildUrl(String sport) {
        return baseUrl.replace("{sport}", sport);
    }

    public String buildUrl() {
        return buildUrl(defaultSport);
    }

    public URI buildUri(String apiKey, String sport) {
        return UriComponentsBuilder.fromHttpUrl(buildUrl(sport))
                .queryParam("apiKey", apiKey)
                .queryParam("regions", regions)
                .queryParam("markets", markets)
                .build()
                .toUri();
    }

    public List<Game> fetchOdds(String apiKey) {
        return fetchOdds(apiKey, defaultSport);
    }

    public List<Game> fetchOdds(String apiKey, String sport) {
        URI uri = buildUri(apiKey, sport);
        ResponseEntity<List<Game>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
