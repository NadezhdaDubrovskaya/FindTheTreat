package com.rmw.selfeducation;

class Innovations {

    private static int connectionInnovationNumber;
    private static int nodeInnovationNumber;

    private Innovations() {
        // used as utility, instantiation isn't necessary
    }

    static int newConnectionInnovationNumber() {
        return connectionInnovationNumber++;
    }

    static int newNodeInnovationNumber() {
        return nodeInnovationNumber++;
    }

    /**
     * Resets both connectionInnovationNumber and nodeInnovationNumber
     */
    static void reset() {
        connectionInnovationNumber = 0;
        nodeInnovationNumber = 0;
    }
}
