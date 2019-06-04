package com.rmw.selfeducation;

import processing.core.PApplet;

import static com.rmw.selfeducation.Configuration.SCALE;

class RectangularObject extends ScreenObject {

    private int width = SCALE;
    private int height = SCALE;

    RectangularObject(final PApplet pApplet) {
        super(pApplet);
        setColour(100, 150, 0);
    }

    RectangularObject(final PApplet pApplet, final Colour colour) {
        super(pApplet);
        setColour(colour.v1, colour.v2, colour.v3);
    }

    @Override
    void show() {
        if (getTile() != null) {
            final int xCoordinate = getTile().getColumn() * SCALE;
            final int yCoordinate = getTile().getRow() * SCALE;
            setVectorPosition(xCoordinate, yCoordinate);
        }
        getPApplet().fill(getColour().v1, getColour().v2, getColour().v3);
        getPApplet().stroke(0, 0, 0);
        getPApplet().rect(getVectorPosition().x, getVectorPosition().y, width, height);
    }

    int getWidth() {
        return width;
    }

    void setWidth(final int width) {
        this.width = width;
    }

    int getHeight() {
        return height;
    }

    void setHeight(final int height) {
        this.height = height;
    }
}
