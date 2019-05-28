package com.rmw.selfeducation;

import processing.core.PApplet;
import processing.core.PVector;

abstract class ScreenObject {

    private final PApplet pApplet;
    private final PVector vectorPosition = new PVector();
    private final Colour colour;
    private Tile tile;

    ScreenObject(final PApplet pApplet) {
        this.pApplet = pApplet;
        colour = new Colour(255, 255, 255);
    }

    Tile getTile() {
        return tile;
    }

    void setTile(final Tile tile) {
        this.tile = tile;
    }

    PApplet getPApplet() {
        return pApplet;
    }

    PVector getVectorPosition() {
        return vectorPosition;
    }

    void setVectorPosition(final float x, final float y) {
        vectorPosition.x = x;
        vectorPosition.y = y;
    }

    Colour getColour() {
        return colour;
    }

    void setColour(final int v1, final int v2, final int v3) {
        colour.v1 = v1;
        colour.v2 = v2;
        colour.v3 = v3;
    }

    void reset() {
        //do nothing by default
    }

    void update() {
        show();
    }

    abstract void show();

}
