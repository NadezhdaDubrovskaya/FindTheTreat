package com.rmw.selfeducation;

import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.COLS;
import static com.rmw.selfeducation.Configuration.ROWS;

/**
 * This class generates and holds the tiles for each square on the game screen
 * Also contains various useful method that can be utilized by other classes such as
 * retrieval of a tile under the passed matrix coordinates, search of a treat tile
 * or determining if there is a wall to the side of the passed matrix coordinates
 */
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
        if (i < 0 || i >= ROWS || j < 0 || j >= COLS) {
            return null;
        }
        return tiles.get(i).get(j);
    }

    Tile getTreatTile() {
        for (final List<Tile> row : tiles) {
            final Tile treatTile = row.stream().filter(Tile::isTreat).findFirst().orElse(null);
            if (treatTile != null) {
                return treatTile;
            }
        }
        return null;
    }

    boolean isTreatTile(final int i, final int j) {
        return tiles.get(i).get(j).isTreat();
    }

    boolean isWallTile(final int i, final int j) {
        return tiles.get(i).get(j).isWall();
    }

}
