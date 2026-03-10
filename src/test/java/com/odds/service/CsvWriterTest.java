package com.odds.service;

import com.odds.model.OddsRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvWriterTest {

    private final CsvWriter csvWriter = new CsvWriter();

    @Test
    void generateFilename_matchesExpectedPattern() {
        String filename = csvWriter.generateFilename();
        assertTrue(filename.matches("odds_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.csv"));
    }

    @Test
    void writeCsv_emptyRows_writesOnlyHeader(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.csv");
        csvWriter.writeCsv(List.of(), file.toString());

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals("home_team,away_team,bookmaker,outcome,price", lines.get(0));
    }

    @Test
    void writeCsv_withRows_writesHeaderAndData(@TempDir Path tempDir) throws IOException {
        OddsRow row1 = new OddsRow("Lakers", "Celtics", "Bet365", "Lakers", 1.5);
        OddsRow row2 = new OddsRow("Lakers", "Celtics", "Bet365", "Celtics", 2.5);

        Path file = tempDir.resolve("test.csv");
        csvWriter.writeCsv(List.of(row1, row2), file.toString());

        List<String> lines = Files.readAllLines(file);
        assertEquals(3, lines.size());
        assertEquals("home_team,away_team,bookmaker,outcome,price", lines.get(0));
        assertEquals("Lakers,Celtics,Bet365,Lakers,1.5", lines.get(1));
        assertEquals("Lakers,Celtics,Bet365,Celtics,2.5", lines.get(2));
    }

    @Test
    void writeCsv_createsFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.csv");
        csvWriter.writeCsv(List.of(), file.toString());
        assertTrue(Files.exists(file));
    }

    @Test
    void writeCsv_handlesSpecialCharacters(@TempDir Path tempDir) throws IOException {
        OddsRow row = new OddsRow("Team, A", "Team B", "Bookie \"X\"", "Draw", 3.0);

        Path file = tempDir.resolve("test.csv");
        csvWriter.writeCsv(List.of(row), file.toString());

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("\"Team, A\",Team B,\"Bookie \"\"X\"\"\",Draw,3.0", lines.get(1));
    }
}
