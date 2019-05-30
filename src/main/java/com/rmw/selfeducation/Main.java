package com.rmw.selfeducation;

import org.apache.commons.io.IOUtils;
import processing.core.PApplet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.COLS;
import static com.rmw.selfeducation.Configuration.ROWS;
import static com.rmw.selfeducation.Configuration.SCALE;
import static java.util.Arrays.stream;

public class Main extends PApplet {

    private final ScreenObject player = new CircularObject(this);
    private final List<ScreenObject> mapTiles = new ArrayList<>();
    private final GameScreen gameScreen = new GameScreen();

    public static void main(final String[] args) {
        PApplet.main("com.rmw.selfeducation.Main", args);
    }

    @Override
    public void settings() {
        size(COLS * SCALE, ROWS * SCALE);
    }

    @Override
    public void setup() {
        final String[] mapConfiguration = getMapConfiguration();

        // generate map tiles in accordance to the map configuration and assign the corresponding tiles
        // from the game screen to each
        // also configure the wall and the treat tile
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                final char tileSetting = mapConfiguration[i].toCharArray()[j];
                final Tile requiredTire = gameScreen.getTileAtPosition(i, j);
                final Colour colour = new Colour(0, 0, 0);
                switch (tileSetting) {
                    case '0':
                        colour.changeColour(175, 190, 190);
                        break;
                    case '1':
                        requiredTire.setWall(true);
                        break;
                    case 'X':
                        colour.changeColour(165, 245, 85);
                        requiredTire.setTreat(true);
                        break;
                    default:
                        throw new InvalidParameterException("Got invalid tile configuration " + tileSetting);
                }
                final RectangularObject rectangle = new RectangularObject(this, colour);
                mapTiles.add(rectangle);
                rectangle.setTile(requiredTire);
            }
        }
        final Tile startingTile = gameScreen.getTileAtPosition(5, 5);
        player.setTile(startingTile);
    }

    @Override
    public void draw() {
        background(0, 0, 0);
        mapTiles.forEach(ScreenObject::update);
        player.update();
    }

    @Override
    public void keyPressed() {
        final Tile newTile;
        final Tile currentTile = player.getTile();
        final int currentRow = currentTile.getRow();
        final int currentColumn = currentTile.getColumn();
        switch (keyCode) {
            case LEFT:
                newTile = gameScreen.getTileAtPosition(currentRow, currentColumn - 1);
                break;
            case RIGHT:
                newTile = gameScreen.getTileAtPosition(currentRow, currentColumn + 1);
                break;
            case UP:
                newTile = gameScreen.getTileAtPosition(currentRow - 1, currentColumn);
                break;
            case DOWN:
                newTile = gameScreen.getTileAtPosition(currentRow + 1, currentColumn);
                break;
            default:
                return;
        }
        if (newTile == null || newTile.isWall()) {
            return;
        }
        player.setTile(null);
        currentTile.setScreenObject(null);
        newTile.setScreenObject(player);
        player.setTile(newTile);
    }

    private String[] getMapConfiguration() {
        try {
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("map.conf");
            final String mapString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final String[] mapSplittedByLines = mapString.split("\r\n");
            if (mapSplittedByLines.length != ROWS) {
                throw new IllegalArgumentException("Map configuration doesn't match with the provided rows configuration");
            }
            if (stream(mapSplittedByLines).anyMatch(s -> s.length() != COLS)) {
                throw new IllegalArgumentException("Map configuration doesn't match with the provided columns configuration");
            }
            if (stream(mapSplittedByLines).anyMatch(s -> !s.matches("[0-1,X]+"))) {
                throw new IllegalArgumentException("Map configuration contains not allowed characters");
            }
            inputStream.close();
            return mapSplittedByLines;
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load the map configuration");
        }
    }
}

