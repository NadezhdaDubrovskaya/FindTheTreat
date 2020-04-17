package com.rmw.selfeducation;

import processing.core.PApplet;

import static com.rmw.selfeducation.Configuration.INPUT_NEURONS_AMOUNT;
import static com.rmw.selfeducation.Configuration.OUTPUT_NEURONS_AMOUNT;
import static com.rmw.selfeducation.Innovations.newNodeInnovationNumber;
import static com.rmw.selfeducation.NeuronType.*;

/**
 * This generator creates genomes based on the current needs.
 * When the initial population is generated, all genomes will look the same,
 * the only difference is the connection's weight, and the innovation numbers are reset for each genome initialization.
 *
 * But for later genomes creation we require to keep the innovation numbers intact
 * (they are to be increased only during mutations)
 * So another generation procedure should be used, the one that combines two different genes together
 */
class GenomeGenerator {

    private GenomeGenerator() {
    }

    /**
     * Generates initial genome.
     * Note that innovation number is renewed each time this method is called so it should be used only
     * during the initial population generation
     *
     * Fresh network always contains:
     * 2 layers of nodes - one for inputs and another one for outputs
     * Among input node a bias neuron is added
     * All input node are connected to the output node with random weight assigned
     *
     * @return fresh genome according to the initial configuration
     */
    static Genome getInitialGenome() {

        final Genome genome = new Genome();

        // it is assumed, that we create a genome like this only for the initial population generation
        // thus we do not really care about the innovation number yet
        Innovations.reset();
        // add bias neuron to the network first
        final NodeGene bias = new NodeGene(newNodeInnovationNumber(), BIAS);
        bias.setLayer(0);
        bias.addSumValue(1f); //bias neuron always has an output value of 1
        genome.getNodes().put(bias.getId(), bias);

        // generate input nodes
        for (int i = 0; i < INPUT_NEURONS_AMOUNT; i++) {
            final int nodeInnovationNumber = newNodeInnovationNumber();
            genome.getNodes().put(nodeInnovationNumber, new NodeGene(nodeInnovationNumber, INPUT));
            genome.getNodes().get(nodeInnovationNumber).setLayer(0);
        }

        // generate output nodes
        for (int i = 0; i < OUTPUT_NEURONS_AMOUNT; i++) {
            final int nodeInnovationNumber = newNodeInnovationNumber();
            genome.getNodes().put(nodeInnovationNumber, new NodeGene(nodeInnovationNumber, OUTPUT));
            genome.getNodes().get(nodeInnovationNumber).setLayer(1);
        }

        genome.firstInitialization();
        return genome;
    }

    static Genome getChildGenome(final Player parent1, final Player parent2) {
        final Genome childGenome = new Genome();
        // if parent2 is more fit than parent 1 - swap them
        final Genome parent1Genome = parent2.getFitness() < parent1.getFitness() ? parent2.getGenome() : parent1.getGenome();
        final Genome parent2Genome = parent2.getFitness() < parent1.getFitness() ? parent1.getGenome() : parent2.getGenome();

        //TODO: implement case for equal fitnesses

        parent1Genome.getConnections().values().forEach(parent1ConnectionGene -> {
            final ConnectionGene parent2MatchingGene = parent2Genome.getConnections().get(parent1ConnectionGene.getInnovationNumber());
            if (parent2MatchingGene != null) {
                // randomly choose a gene from either parent 1 or 2
                final ConnectionGene randomGene = Utils.randomBoolean() ? parent1ConnectionGene : parent2MatchingGene;
                childGenome.getConnections().put(randomGene.getInnovationNumber(), randomGene.copy());
            }
        });


        // add only nodes that are common to both parents
        // TODO temp solution
        parent1Genome.getNodes().values().forEach(node -> {
            if (parent2Genome.getNodes().containsKey(node.getId())) {
                childGenome.getNodes().put(node.getId(), node.copy());
            }
        });

        childGenome.setAmountOfLayers(parent1Genome.getAmountOfLayers());
        childGenome.updateNodesByLayers();

        return childGenome;
    }

}
