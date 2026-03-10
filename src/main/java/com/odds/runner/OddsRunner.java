package com.odds.runner;

import com.odds.model.Game;
import com.odds.model.OddsRow;
import com.odds.service.CsvWriter;
import com.odds.service.OddsFetcher;
import com.odds.service.OddsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OddsRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OddsRunner.class);

    private final OddsFetcher oddsFetcher;
    private final OddsParser oddsParser;
    private final CsvWriter csvWriter;

    public OddsRunner(OddsFetcher oddsFetcher, OddsParser oddsParser, CsvWriter csvWriter) {
        this.oddsFetcher = oddsFetcher;
        this.oddsParser = oddsParser;
        this.csvWriter = csvWriter;
    }

    @Override
    public void run(String... args) {
        String apiKey = System.getenv("ODDS_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            log.error("Error: the ODDS_API_KEY environment variable is not set.");
            return;
        }

        List<Game> games;
        try {
            games = oddsFetcher.fetchOdds(apiKey);
        } catch (Exception ex) {
            log.error("Error fetching odds from {}: {}", oddsFetcher.buildUrl(), ex.getMessage());
            return;
        }

        if (games == null || games.isEmpty()) {
            log.info("No games returned from API.");
            return;
        }

        List<OddsRow> rows = oddsParser.parseOdds(games);

        printOdds(rows);

        String filename = csvWriter.generateFilename();
        try {
            csvWriter.writeCsv(rows, filename);
            log.info("Results exported to {}", filename);
        } catch (Exception ex) {
            log.error("Error creating CSV file {}: {}", filename, ex.getMessage());
        }
    }

    private void printOdds(List<OddsRow> rows) {
        Map<String, List<OddsRow>> grouped = new LinkedHashMap<>();
        for (OddsRow row : rows) {
            String key = row.getHomeTeam() + " vs " + row.getAwayTeam();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        for (Map.Entry<String, List<OddsRow>> entry : grouped.entrySet()) {
            System.out.println(entry.getKey());
            for (OddsRow row : entry.getValue()) {
                System.out.println("  Casa: " + row.getBookmaker());
                System.out.println("    " + row.getOutcome() + " " + row.getPrice());
            }
            System.out.println();
        }
    }
}
