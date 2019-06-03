package com.rmw.selfeducation;

import org.junit.Test;

public class Sandbox {

    @Test
    public void testAddNodeMutation() {
        final Genome genome = new Genome();
        genome.addNodeMutation();
        genome.addNodeMutation();
        genome.addNodeMutation();
    }

}
