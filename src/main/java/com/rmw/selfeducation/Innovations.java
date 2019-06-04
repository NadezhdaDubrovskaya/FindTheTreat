package com.rmw.selfeducation;

class Innovations {

    private static int connectionInnovationNumber;
    /**
     * I am not sure whether we really need an innovation number for the node but let's try this out
     */
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
