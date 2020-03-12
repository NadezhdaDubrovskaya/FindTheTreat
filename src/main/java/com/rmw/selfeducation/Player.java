package com.rmw.selfeducation;

import processing.core.PApplet;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.rmw.selfeducation.Configuration.PLAYER_START_X_POSITION;
import static com.rmw.selfeducation.Configuration.PLAYER_START_Y_POSITION;
import static com.rmw.selfeducation.Configuration.RED;

class Player extends CircularObject {

    private Genome genome;
    // player has to know about game screen to be able to see obstacles and the location of the treat
    private GameScreen gameScreen;
    private float fitness;
    private boolean dead;

    Player(final PApplet pApplet, final Genome genome, final GameScreen gameScreen) {
        super(pApplet);
        this.genome = genome;
        this.gameScreen = gameScreen;
        reset();
    }

    boolean isAlive() {
        return !dead;
    }

    void setGenome(final Genome genome) {
        this.genome = genome;
    }

    void setGameScreen(final GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    void update() {
        if (!dead) {
            look();
            react();
            checkIfDied();
        }
        super.update();
    }

    @Override
    void reset() {
        fitness = 0;
        dead = false;
        setTile(gameScreen.getTileAtPosition(PLAYER_START_X_POSITION, PLAYER_START_Y_POSITION));
    }

    private void look() {
        final Map<Integer, Float> inputs = new TreeMap<>();
        final int currentRow = getTile().getRow();
        final int currentColumn = getTile().getColumn();

        // populate inputs related to the walls
        final boolean wallOnTheLeft = gameScreen.isWallTile(currentRow, currentColumn - 1);
        final boolean wallOnTheRight = gameScreen.isWallTile(currentRow, currentColumn + 1);
        final boolean wallAtTheTop = gameScreen.isWallTile(currentRow - 1, currentColumn);
        final boolean wallAtTheBottom = gameScreen.isWallTile(currentRow + 1, currentColumn);

        inputs.put(1, wallOnTheLeft ? 1f : 0f);
        inputs.put(2, wallOnTheRight ? 1f : 0f);
        inputs.put(3, wallAtTheTop ? 1f : 0f);
        inputs.put(4, wallAtTheBottom ? 1f : 0f);

        // populate inputs related to the treat
        final Tile treatTile = gameScreen.getTreatTile();
        final float treatIsToTheLeft = currentColumn > treatTile.getColumn() ? 1f : 0f;
        final float treatIsToTheRight = currentColumn < treatTile.getColumn() ? 1f : 0f;
        final float treatIsAtTheTop = currentRow > treatTile.getRow() ? 1f : 0f;
        final float treatIsAtTheBottom = currentRow < treatTile.getRow() ? 1f : 0f;

        inputs.put(5, treatIsToTheLeft);
        inputs.put(6, treatIsToTheRight);
        inputs.put(7, treatIsAtTheTop);
        inputs.put(8, treatIsAtTheBottom);

        genome.setInputs(inputs);
    }

    private void react() {
        final List<Float> reaction = genome.feedForward();
        if (reaction.size() != 2) {
            throw new IllegalArgumentException("Something is wrong, expected 2 output nodes");
        }
        final int changeOnX = Math.round(reaction.get(0));
        final int changeOnY = Math.round(reaction.get(1));
        if ((changeOnX != 1 && changeOnX != 0 && changeOnX != -1) || (changeOnY != 1 && changeOnY != 0 && changeOnY != -1)) {
            throw new IllegalArgumentException("Something is wrong with the network, expected 1,0 or -1 as an output");
        }
        final Tile newTile = gameScreen.getTileAtPosition(getTile().getRow() + changeOnY, getTile().getColumn() + changeOnX);
        if (newTile != null) {
            setTile(newTile);
        }
    }

    /**
     * This method is required because when player reacts according to its ANN response, it doesn't check
     * whether the direction is a wall or not, thus it can occasionally try to run through the walls.
     * To punish such a behaviour the concept of death is introduced.
     * Whenever ANN made a decision to go through the wall the player dies and cannot move no more.
     * Thus only the networks that figured out how to avoid hitting the walls will reproduce.
     */
    private void checkIfDied() {
        if (getTile().isWall()) {
            dead = true;
            setColour(RED.v1, RED.v2, RED.v3);
        }
    }
}
