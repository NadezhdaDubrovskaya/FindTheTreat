package com.rmw.selfeducation;

import processing.core.PApplet;

import static com.rmw.selfeducation.Configuration.SCALE;

class CircularObject extends ScreenObject {

    private float radius = (float) SCALE / 3;

    CircularObject(final PApplet pApplet) {
        super(pApplet);
    }

    float getRadius() {
        return radius;
    }

    void setRadius(final int radius) {
        this.radius = radius;
    }

    @Override
    void show() {
        if (getTile() != null) {
            final int xCoordinate = getTile().getColumn() * SCALE + SCALE / 2;
            final int yCoordinate = getTile().getRow() * SCALE + SCALE / 2;
            setVectorPosition(xCoordinate, yCoordinate);
            getPApplet().fill(getColour().v1, getColour().v2, getColour().v3);
            getPApplet().ellipse(getVectorPosition().x, getVectorPosition().y, radius * 2, radius * 2);
        }
    }

}
