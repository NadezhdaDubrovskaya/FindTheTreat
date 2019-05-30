package com.rmw.selfeducation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.rmw.selfeducation.NeuronType.BIAS;
import static com.rmw.selfeducation.NeuronType.INPUT;
import static com.rmw.selfeducation.NeuronType.OUTPUT;

class Genome {

    private final int amountOfInputNodes = 8;
    private final int amountOfOutputNodes = 2;

    private final Random r = new Random();
    private List<NodeGene> nodes = new ArrayList<>();
    private List<ConnectionGene> connections = new ArrayList<>();

    Genome() {
        // add bias neuron to the network first
        nodes.add(new NodeGene(0, BIAS));
        nodes.get(0).setLayer(0);

        // generate input nodes
        for (int i = 0; i < amountOfInputNodes; i++) {
            nodes.add(new NodeGene(nodes.size(), INPUT));
            nodes.get(nodes.size() - 1).setLayer(0);
        }

        // generate output nodes
        for (int i = 0; i < amountOfOutputNodes; i++) {
            nodes.add(new NodeGene(nodes.size(), OUTPUT));
            nodes.get(nodes.size() - 1).setLayer(1);
        }
    }

    void generateNetwork() {

    }

    void addConnectionMutation() {
        NodeGene node1 = nodes.get(r.nextInt(nodes.size()));
        NodeGene node2 = nodes.get(r.nextInt(nodes.size()));
        while (nodesAreNotGood(node1, node2)) {
            node1 = nodes.get(r.nextInt(nodes.size()));
            node2 = nodes.get(r.nextInt(nodes.size()));
        }

        // checks if nodes have to be swapped since only the "left-to-right" connections are allowed
        if (node1.getLayer() > node2.getLayer()) {
            final NodeGene temp = node2;
            node2 = node1;
            node1 = temp;
        }

        //TODO implement innovation number
        final ConnectionGene newConnection = new ConnectionGene(node1.getId(), node2.getId(), r.nextFloat() * 2f - 1f, true, 0);
        connections.add(newConnection);
        node1.addOutgoingConnection(newConnection);
    }

    void addNodeMutation() {

    }

    /**
     * Check if the chosen nodes are fit for add connection mutation or not
     * That means that they:
     * - should not be the same node,
     * - should not be on the same layer,
     * - should not be already connected,
     * - neither should be bias node
     */
    private boolean nodesAreNotGood(final NodeGene node1, final NodeGene node2) {
        final boolean nodesAreTheSame = node1.equals(node2);
        final boolean nodesAreOnTheSameLayer = node1.getLayer() == node2.getLayer();
        final boolean nodesAreAlreadyConnected = node1.isConnectedTo(node2);
        final boolean oneOfTheNodesIsBias = node1.getType() == BIAS || node2.getType() == BIAS;
        return nodesAreTheSame || nodesAreOnTheSameLayer || nodesAreAlreadyConnected || oneOfTheNodesIsBias;
    }
}
