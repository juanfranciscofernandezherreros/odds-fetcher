package com.odds.service;

import com.odds.model.OddsRow;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvWriter {

    private static final String HEADER = "home_team,away_team,bookmaker,outcome,price";
    private static final int BUFFER_SIZE = 65536;
    private static final DateTimeFormatter FILENAME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").withZone(ZoneOffset.UTC);

    public String generateFilename() {
        return "odds_" + FILENAME_FORMAT.format(Instant.now()) + ".csv";
    }

    public void writeCsv(List<OddsRow> rows, String filename) throws IOException {
        Path path = Path.of(filename);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8),
                BUFFER_SIZE)) {

            writer.write(HEADER);
            writer.write('\n');

            for (OddsRow row : rows) {
                writer.write(
                        escapeCsv(row.getHomeTeam()) + ","
                        + escapeCsv(row.getAwayTeam()) + ","
                        + escapeCsv(row.getBookmaker()) + ","
                        + escapeCsv(row.getOutcome()) + ","
                        + row.getPrice() + "\n");
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
