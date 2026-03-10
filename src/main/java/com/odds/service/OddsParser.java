package com.odds.service;

import com.odds.model.Bookmaker;
import com.odds.model.Game;
import com.odds.model.Market;
import com.odds.model.OddsRow;
import com.odds.model.Outcome;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OddsParser {

    public List<OddsRow> parseOdds(List<Game> games) {
        List<OddsRow> rows = new ArrayList<>();
        for (Game game : games) {
            String home = game.getHomeTeam();
            String away = game.getAwayTeam();
            for (Bookmaker bookmaker : game.getBookmakers()) {
                String title = bookmaker.getTitle() != null ? bookmaker.getTitle() : "Unknown";
                for (Market market : bookmaker.getMarkets()) {
                    for (Outcome outcome : market.getOutcomes()) {
                        rows.add(new OddsRow(home, away, title, outcome.getName(), outcome.getPrice()));
                    }
                }
            }
        }
        return rows;
    }
}
