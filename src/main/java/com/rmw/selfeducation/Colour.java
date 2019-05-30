package com.rmw.selfeducation;

/**
 * Represents an RGB colour that is used in the filling of processing visual objects
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

}
