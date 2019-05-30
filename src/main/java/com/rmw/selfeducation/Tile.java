package com.rmw.selfeducation;


class Tile {

    private final Integer row;
    private final Integer column;
    private ScreenObject screenObject;
    /**
     * indicates if the tile is a wall
     */
    private boolean wall;
    /**
     * indicates if the tile is a treat
     */
    private boolean treat;

    Tile(final Integer row, final Integer column) {
        this.row = row;
        this.column = column;
        wall = false;
        treat = false;
    }

    /**
     * Updates a screenObject that it contains
     */
    void update() {
        if (screenObject != null) {
            screenObject.update();
        }
    }

    ScreenObject getScreenObject() {
        return screenObject;
    }

    void setScreenObject(final ScreenObject screenObject) {
        this.screenObject = screenObject;
    }

    Integer getRow() {
        return row;
    }

    Integer getColumn() {
        return column;
    }

    boolean isWall() {
        return wall;
    }

    void setWall(final boolean wall) {
        this.wall = wall;
    }

    boolean isTreat() {
        return treat;
    }

    void setTreat(final boolean treat) {
        this.treat = treat;
    }
}
