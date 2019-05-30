package com.rmw.selfeducation;

class Configuration {

    /**
     * If false then it will behave as a regular game and player will be given an opportunity to run and avoid AIs
     * If true then it means the game is in the mode of teaching AIs
     */
    static final boolean IS_AI_MODE = true;

    static final int ROWS = 15;
    static final int COLS = 25;

    /**
     * Sets the correlation between coordinates (pixels) and the array of tiles.
     * For example, if the scale is set to 50 then it means each tile will be a square of 50 "pixels".
     * Meaning that a tile array 10x15 in size will require a screen of 500x750 px
     */
    static final int SCALE = 30;

    private Configuration() {
    }

}
