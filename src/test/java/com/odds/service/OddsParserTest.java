package com.odds.service;

import com.odds.model.Bookmaker;
import com.odds.model.Game;
import com.odds.model.Market;
import com.odds.model.OddsRow;
import com.odds.model.Outcome;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OddsParserTest {

    private final OddsParser parser = new OddsParser();

    @Test
    void parseOdds_emptyList_returnsEmptyList() {
        List<OddsRow> rows = parser.parseOdds(Collections.emptyList());
        assertTrue(rows.isEmpty());
    }

    @Test
    void parseOdds_nullList_returnsEmptyList() {
        List<OddsRow> rows = parser.parseOdds(null);
        assertTrue(rows.isEmpty());
    }

    @Test
    void parseOdds_gameWithNoBookmakers_returnsEmptyList() {
        Game game = new Game("Lakers", "Celtics", Collections.emptyList());
        List<OddsRow> rows = parser.parseOdds(List.of(game));
        assertTrue(rows.isEmpty());
    }

    @Test
    void parseOdds_singleGameSingleBookmakerTwoOutcomes() {
        Outcome o1 = new Outcome("Lakers", 1.5);
        Outcome o2 = new Outcome("Celtics", 2.5);
        Market market = new Market("h2h", List.of(o1, o2));
        Bookmaker bookmaker = new Bookmaker("Bet365", List.of(market));
        Game game = new Game("Lakers", "Celtics", List.of(bookmaker));

        List<OddsRow> rows = parser.parseOdds(List.of(game));

        assertEquals(2, rows.size());
        assertEquals("Lakers", rows.get(0).getHomeTeam());
        assertEquals("Celtics", rows.get(0).getAwayTeam());
        assertEquals("Bet365", rows.get(0).getBookmaker());
        assertEquals("Lakers", rows.get(0).getOutcome());
        assertEquals(1.5, rows.get(0).getPrice());
        assertEquals("Celtics", rows.get(1).getOutcome());
        assertEquals(2.5, rows.get(1).getPrice());
    }

    @Test
    void parseOdds_multipleBookmakers() {
        Outcome o1 = new Outcome("Lakers", 1.5);
        Outcome o2 = new Outcome("Celtics", 2.5);
        Market market1 = new Market("h2h", List.of(o1, o2));
        Bookmaker bk1 = new Bookmaker("Bet365", List.of(market1));

        Outcome o3 = new Outcome("Lakers", 1.6);
        Outcome o4 = new Outcome("Celtics", 2.4);
        Market market2 = new Market("h2h", List.of(o3, o4));
        Bookmaker bk2 = new Bookmaker("William Hill", List.of(market2));

        Game game = new Game("Lakers", "Celtics", List.of(bk1, bk2));
        List<OddsRow> rows = parser.parseOdds(List.of(game));

        assertEquals(4, rows.size());
        assertEquals("Bet365", rows.get(0).getBookmaker());
        assertEquals("William Hill", rows.get(2).getBookmaker());
    }

    @Test
    void parseOdds_eachRowHasAllFields() {
        Outcome o1 = new Outcome("Lakers", 1.5);
        Market market = new Market("h2h", List.of(o1));
        Bookmaker bookmaker = new Bookmaker("Bet365", List.of(market));
        Game game = new Game("Lakers", "Celtics", List.of(bookmaker));

        List<OddsRow> rows = parser.parseOdds(List.of(game));

        assertEquals(1, rows.size());
        OddsRow row = rows.get(0);
        assertNotNull(row.getHomeTeam());
        assertNotNull(row.getAwayTeam());
        assertNotNull(row.getBookmaker());
        assertNotNull(row.getOutcome());
        assertNotNull(row.getPrice());
    }
}
