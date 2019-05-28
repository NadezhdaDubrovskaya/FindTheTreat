package com.rmw.selfeducation;

import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.COLS;
import static com.rmw.selfeducation.Configuration.ROWS;

class GameScreen {

    private final List<List<Tile>> tiles = new ArrayList<>(ROWS);

    GameScreen() {
        init();
    }

    private void init() {
        for (int i = 0; i < ROWS; i++) {
            final List<Tile> row = new ArrayList<>();
            for (int j = 0; j < COLS; j++) {
                final Tile tile = new Tile(i, j);
                row.add(tile);
            }
            tiles.add(row);
        }
    }

    Tile getTileAtPosition(final int i, final int j) {
        return tiles.get(i).get(j);
    }

}
