package com.rmw.selfeducation;

public enum GameMode {

    /**
     * The full AI mode in which we trains the network using NEAT
     * User has no control over the genome.
     * Draw Network Utility displays the best Genome in the current population
     */
    NEAT,

    /**
     * The regular game where a user can control the player.
     * The genome has no effect on the players reaction.
     */
    MANUAL_GAME,

    /**
     * One player the actions of which are controlled by the genome
     * No population, no evolution, no manual control over the player
     * Draw Netwrok Utility displayed the current genome of the player.
     * It is possible to change the genome manually (add nodes and connections, generate random wights)
     * and restart the player to see how it performs
     */
    MANUAL_AN

}
