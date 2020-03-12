package com.rmw.selfeducation;


class Tile {

    private final Integer row;
    private final Integer column;
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

    void setEmpty() {
        wall = false;
        treat = false;
    }

    void setTreat() {
        wall = false;
        treat = true;
    }

    void setWall() {
        treat = false;
        wall = true;
    }

    boolean isTreat() {
        return treat;
    }

}
