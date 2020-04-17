package com.rmw.selfeducation;

import org.junit.Test;

import java.util.Random;

public class Sandbox {

    private final Random r = new Random();

    @Test
    public void testAddNodeMutation() {
        final Genome genome = GenomeGenerator.getInitialGenome();
        genome.addNodeMutation();
        genome.addNodeMutation();
        genome.addNodeMutation();
    }

}
