package com.rmw.selfeducation;

/**
 * Represents RGB colour that is used by Processing
 */
class Colour {

    int v1;
    int v2;
    int v3;

    Colour(final int v1, final int v2, final int v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    void changeColour(final int v1, final int v2, final int v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    void changeColour(final Colour colour) {
        v1 = colour.v1;
        v2 = colour.v2;
        v3 = colour.v3;
    }
}
