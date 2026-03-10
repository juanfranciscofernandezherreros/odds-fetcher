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
    private static final char SEPARATOR = ',';
    private static final char NEWLINE = '\n';
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
            writer.write(NEWLINE);

            StringBuilder sb = new StringBuilder(256);
            for (OddsRow row : rows) {
                sb.setLength(0);
                appendEscaped(sb, row.getHomeTeam());
                sb.append(SEPARATOR);
                appendEscaped(sb, row.getAwayTeam());
                sb.append(SEPARATOR);
                appendEscaped(sb, row.getBookmaker());
                sb.append(SEPARATOR);
                appendEscaped(sb, row.getOutcome());
                sb.append(SEPARATOR);
                sb.append(row.getPrice());
                sb.append(NEWLINE);
                writer.write(sb.toString());
            }
        }
    }

    private void appendEscaped(StringBuilder sb, String value) {
        if (value == null) {
            return;
        }
        if (value.indexOf(SEPARATOR) >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0) {
            sb.append('"');
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '"') {
                    sb.append('"');
                }
                sb.append(c);
            }
            sb.append('"');
        } else {
            sb.append(value);
        }
    }
}
