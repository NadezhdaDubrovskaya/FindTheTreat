package com.rmw.selfeducation;

class Configuration {

    /**
     * If false then it will behave as a regular game and player will be given an opportunity to run and avoid AIs
     * If true then it means the game is in the mode of teaching AIs
     */
    static final boolean IS_AI_MODE = true;

    static final int WIDTH = 1500;
    static final int HEIGHT = 800;

    static final int ROWS = 15;
    static final int COLS = 25;

    /**
     * Sets the correlation between coordinates (pixels) and the array of tiles.
     * For example, if the scale is set to 50 then it means each tile will be a square of 50 "pixels".
     * Meaning that a tile array 10x15 in size will require a screen of 500x750 px
     */
    static final int SCALE = 30;
    static final int PLAYER_START_X_POSITION = 5;
    static final int PLAYER_START_Y_POSITION = 5;

    // genetic algorithm related settings
    static final int AMOUNT_OF_PLAYERS_IN_POPULATION = 100;

    // Map configuration
    static final char EMPTY_TILE = '0';
    static final char WALL_TILE = '1';
    static final char TREAT_TILE = 'X';

    //Neural network graphical representation screen positioning
    static final float START_X = COLS * SCALE + 50f;
    static final float START_Y = 0;
    static final float ANN_WIDTH = WIDTH * 0.95f - START_X;
    static final float ANN_HEIGHT = (float) ROWS * SCALE;
    static final float NODE_SIZE = 25; //radius

    //ANN configuration
    static final int INPUT_NEURONS_AMOUNT = 8;
    static final int OUTPUT_NEURONS_AMOUNT = 2;

    // Colors
    static final Colour BLACK = new Colour(0, 0, 0);
    static final Colour WHITE = new Colour(255, 255, 255);
    static final Colour RED = new Colour(215, 50, 10);
    static final Colour BLUE = new Colour(10, 50, 215);
    static final Colour LIGHT_GREEN = new Colour(157, 229, 84);
    static final Colour MEDIUM_GRAY = new Colour(132, 132, 132);
    static final Colour LIGHT_BLUE = new Colour(155, 215, 245);

    private Configuration() {
    }

}
