package com.rmw.selfeducation;

import org.apache.commons.io.IOUtils;
import processing.core.PApplet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.ANN_HEIGHT;
import static com.rmw.selfeducation.Configuration.ANN_WIDTH;
import static com.rmw.selfeducation.Configuration.BLACK;
import static com.rmw.selfeducation.Configuration.COLS;
import static com.rmw.selfeducation.Configuration.EMPTY_TILE;
import static com.rmw.selfeducation.Configuration.HEIGHT;
import static com.rmw.selfeducation.Configuration.LIGHT_BLUE;
import static com.rmw.selfeducation.Configuration.LIGHT_GREEN;
import static com.rmw.selfeducation.Configuration.MEDIUM_GRAY;
import static com.rmw.selfeducation.Configuration.ROWS;
import static com.rmw.selfeducation.Configuration.SCALE;
import static com.rmw.selfeducation.Configuration.START_X;
import static com.rmw.selfeducation.Configuration.START_Y;
import static com.rmw.selfeducation.Configuration.TREAT_TILE;
import static com.rmw.selfeducation.Configuration.WALL_TILE;
import static com.rmw.selfeducation.Configuration.WHITE;
import static com.rmw.selfeducation.Configuration.WIDTH;
import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

public class Main extends PApplet {

    private final Player player = new Player(this);
    private final List<ScreenObject> mapTiles = new ArrayList<>();
    private final GameScreen gameScreen = new GameScreen();
    private final DrawNetworkUtility drawNetworkUtility = new DrawNetworkUtility(this);
    //control buttons
    private final Button addNodeMutationButton = new Button("Add Node Mutation", 50, ROWS * SCALE + 50, 130, 30);
    private final Button addConnectionMutationButton = new Button("Add Connection Mutation", 50, ROWS * SCALE + 100, 130, 30);
    private final Button generateNewNetworkButton = new Button("Generate New Network", 50, ROWS * SCALE + 150, 130, 30);
    //TODO this is the test genome, remove it later on
    private Genome genome = new Genome();


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
        player.setGenome(genome);
        player.setGameScreen(gameScreen);
    }

    @Override
    public void draw() {
        background(LIGHT_BLUE.v1, LIGHT_BLUE.v2, LIGHT_BLUE.v3);
        mapTiles.forEach(ScreenObject::update);
        player.update();
        drawNeuralNetwork();
        drawControlPanel();
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
        player.setTile(newTile);
    }

    @Override
    public void mousePressed() {
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
        }
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
        final Tile startingTile = gameScreen.getTileAtPosition(6, 5);
        player.setTile(startingTile);
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

