package com.rmw.selfeducation;

import org.apache.commons.io.IOUtils;
import processing.core.PApplet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.*;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

public class Main extends PApplet {

    /*
     * Core game components
     */
    private final List<ScreenObject> mapTiles = new ArrayList<>(); // is used for rendering
    private final GameScreen gameScreen = new GameScreen();
    private final DrawNetworkUtility drawNetworkUtility = new DrawNetworkUtility(this);

    /*
     * Used in MANUAL_AI and MANUAL_GAME game modes
     */
    private Genome genome;
    private Player player;
    private boolean gameFinished;

    /*
     * Used in MANUAL_AI game mode
     */
    private final Button addNodeMutationButton = new Button("Add Node Mutation", 50, ROWS * SCALE + 50, 150, 30);
    private final Button addConnectionMutationButton = new Button("Add Connection Mutation", 50, ROWS * SCALE + 100, 150, 30);
    private final Button generateNewNetworkButton = new Button("Generate New Network", 50, ROWS * SCALE + 150, 150, 30);
    private final Button resetPlayer = new Button("Reset Player", 50, ROWS * SCALE + 200, 150, 30);

    /*
     * Used in NEAT game mode
     */
    private Population population;

    public static void main(final String[] args) {
        PApplet.main("com.rmw.selfeducation.Main", args);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        drawGameScreen();
        if (gameMode.equals(GameMode.MANUAL_GAME) || gameMode.equals(GameMode.MANUAL_AN)) {
            genome = new Genome();
            player = new Player(this, genome, gameScreen);
        }
        if (gameMode.equals(GameMode.NEAT)) {
            population = new Population(this, gameScreen);
        }
    }

    @Override
    public void draw() {
        background(LIGHT_BLUE.v1, LIGHT_BLUE.v2, LIGHT_BLUE.v3);
        mapTiles.forEach(ScreenObject::update);
        switch (gameMode) {
            case NEAT:
                population.update();
                break;
            case MANUAL_AN:
                player.update();
                drawControlPanel();
                drawNeuralNetwork();
                break;
            case MANUAL_GAME:
                player.show();
                break;
            default:
                throw new IllegalArgumentException("Please choose one of the allowed game modes in the configuration");
        }
    }

    @Override
    public void keyPressed() {
        if (!gameMode.equals(GameMode.MANUAL_GAME) || gameFinished) {
            return;
        }

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

        /*
         * Returned tile can be out of bound or a wall to which we cannot go.
         * Thus the player stays where he is and the tile stays the same.
         */
        if (newTile == null || newTile.isWall()) {
            return;
        }
        player.setTile(newTile);
        if (newTile.isTreat()) {
            gameFinished = true;
        }
    }

    @Override
    public void mousePressed() {
        if (!gameMode.equals(GameMode.MANUAL_AN)) {
            return;
        }

        if (addNodeMutationButton.mouseIsOver()) {
            genome.addNodeMutation();
        }
        if (addConnectionMutationButton.mouseIsOver()) {
            genome.addConnectionMutation();
        }
        if (generateNewNetworkButton.mouseIsOver()) {
            Innovations.reset();
            genome = new Genome();
            player.setGenome(genome);
            player.reset();
        }
        if (resetPlayer.mouseIsOver()) {
            player.reset();
        }
    }

    /**
     * Is called during game setup once.
     * After that the
     */
    private void drawGameScreen() {
        final String[] mapConfiguration = getMapConfiguration();
        // generate map tiles in accordance to the map configuration and assign the corresponding tiles
        // from the game screen to each
        // also configure the wall and the treat tile
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                final char tileSetting = mapConfiguration[i].toCharArray()[j];
                final Tile requiredTire = gameScreen.getTileAtPosition(i, j);
                final Colour colour = new Colour(BLACK.v1, BLACK.v2, BLACK.v3);
                switch (tileSetting) {
                    case EMPTY_TILE:
                        colour.changeColour(MEDIUM_GRAY);
                        break;
                    case WALL_TILE:
                        requiredTire.setWall();
                        break;
                    case TREAT_TILE:
                        colour.changeColour(LIGHT_GREEN);
                        requiredTire.setTreat();
                        break;
                    default:
                        throw new InvalidParameterException("Got invalid tile configuration " + tileSetting);
                }
                final RectangularObject rectangle = new RectangularObject(this, colour);
                mapTiles.add(rectangle);
                rectangle.setTile(requiredTire);
            }
        }
    }

    /**
     * Retrieves map configuration from map.conf file that should be located in the resources
     * <p>
     * The file is validated so the configuration from the file corresponds with internal settings
     * of Rows and Cols (In the Configuration class) and contains only valid characters
     *
     * @return map configuration as List of Strings, each string is a set of 0, 1 and X, where:
     * * 0 - represents empty tile
     * * 1 - represents a wall
     * * X - represents a treat
     */
    private String[] getMapConfiguration() {
        try {
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("map.conf");
            assert inputStream != null;
            final String mapString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final String[] mapSplittedByLines = mapString.split("\r\n");
            if (mapSplittedByLines.length != ROWS) {
                throw new IllegalArgumentException("Map configuration doesn't match with the provided rows configuration");
            }
            if (stream(mapSplittedByLines).anyMatch(s -> s.length() != COLS)) {
                throw new IllegalArgumentException("Map configuration doesn't match with the provided columns configuration");
            }
            final String regExp = format("[{0},{1},{2}]+", EMPTY_TILE, WALL_TILE, TREAT_TILE);
            if (stream(mapSplittedByLines).anyMatch(s -> !s.matches(regExp))) {
                throw new IllegalArgumentException("Map configuration contains not allowed characters");
            }
            inputStream.close();
            return mapSplittedByLines;
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load the map configuration");
        }
    }

    // TODO should show currently best performing network but for now showing test network
    private void drawNeuralNetwork() {
        fill(WHITE.v1, WHITE.v2, WHITE.v3);
        rect(START_X, START_Y, ANN_WIDTH, ANN_HEIGHT);
        drawNetworkUtility.draw(genome);
    }

    private void drawControlPanel() {
        addNodeMutationButton.show();
        addConnectionMutationButton.show();
        generateNewNetworkButton.show();
        resetPlayer.show();
    }

    class Button {
        String label;
        float x;
        float y;
        float width;
        float height;

        Button(final String label, final float x, final float y, final float width, final float height) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        void show() {
            fill(LIGHT_GREEN.v1, LIGHT_GREEN.v2, LIGHT_GREEN.v3);
            stroke(BLACK.v1, BLACK.v2, BLACK.v3);
            rect(x, y, width, height, 10);
            textAlign(CENTER, CENTER);
            fill(0);
            text(label, x + (width / 2), y + (height / 2));
        }

        boolean mouseIsOver() {
            return mouseX > x && mouseX < (x + width) && mouseY > y && mouseY < (y + height);
        }
    }
}

