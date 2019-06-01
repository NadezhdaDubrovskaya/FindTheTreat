package com.rmw.selfeducation;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static com.rmw.selfeducation.Innovations.newConnectionInnovationNumber;
import static com.rmw.selfeducation.Innovations.newNodeInnovationNumber;
import static com.rmw.selfeducation.NeuronType.BIAS;
import static com.rmw.selfeducation.NeuronType.HIDDEN;
import static com.rmw.selfeducation.NeuronType.INPUT;
import static com.rmw.selfeducation.NeuronType.OUTPUT;

class Genome {

    private final int amountOfInputNodes = 8;
    private final int amountOfOutputNodes = 2;

    private final Random r = new Random();
    private Map<Integer, NodeGene> nodes = new TreeMap<>();
    private Map<Integer, ConnectionGene> connections = new TreeMap<>();

    Genome() {
        generateInitialGenome();
    }

    /**
     * Mutation during which currently missing connection is added.
     * The connection can be only in the "left-to-right" direction and between different
     * nodes located on the different layers
     * If the network is fully connected, method does nothing
     */
    void addConnectionMutation() {
        //TODO add a check if the network is already fully connected to avoid eternal while

        NodeGene node1 = nodes.get(r.nextInt(nodes.size()));
        NodeGene node2 = nodes.get(r.nextInt(nodes.size()));
        while (nodesAreNotGood(node1, node2)) {
            node1 = nodes.get(r.nextInt(nodes.size()));
            node2 = nodes.get(r.nextInt(nodes.size()));
        }

        // checks if nodes have to be swapped since only the "left-to-right" connections are allowed
        if (node1.getLayer() > node2.getLayer()) {
            addNewConnection(node2, node1, generateRandomWeight());
        } else {
            addNewConnection(node1, node2, generateRandomWeight());
        }
    }

    /**
     * Mutation during which a new node is added.
     * The node is added according to the following algorithm:
     * - Choose random connection and disable it
     * - Add a new node to the list of nodes with new node innovation number
     * - Create two new connections:
     * 1. From in node of the disabled connection to new node with weight equal to 1
     * 2. From new node to out node of the disable connection with disabled connection weight
     */
    void addNodeMutation() {
        ConnectionGene connectionToBeDisabled = connections.get(r.nextInt(connections.size()));
        // if chosen connection is already disabled - select a new one and do until we get valid connection
        while (!connectionToBeDisabled.isExpressed()) {
            connectionToBeDisabled = connections.get(r.nextInt(connections.size()));
        }

        connectionToBeDisabled.disable();

        final NodeGene newNode = new NodeGene(newNodeInnovationNumber(), HIDDEN);
        final NodeGene disabledConnectionInNode = nodes.get(connectionToBeDisabled.getInNode());
        final NodeGene disabledConnectionOutNode = nodes.get(connectionToBeDisabled.getOutNode());
        final float disabledConnectionWeight = connectionToBeDisabled.getWeight();

        realignLayers(disabledConnectionInNode, disabledConnectionOutNode, newNode);

        nodes.put(newNode.getId(), newNode);
        addNewConnection(disabledConnectionInNode, newNode, 1f);
        addNewConnection(newNode, disabledConnectionOutNode, disabledConnectionWeight);
    }

    //TODO implement the functionality of squeezing the new layer in
    private void realignLayers(final NodeGene disabledConnectionInNode, final NodeGene disabledConnectionOutNode, final NodeGene newNode) {
        newNode.setLayer(0);
    }

    private void generateInitialGenome() {
        // add bias neuron to the network first
        final NodeGene bias = new NodeGene(0, BIAS);
        bias.setLayer(0);
        bias.setOutputValue(1f); //bias neuron always has an output value of 1
        nodes.put(0, bias);

        // generate input nodes
        for (int i = 0; i < amountOfInputNodes; i++) {
            final int nodeInnovationNumber = newNodeInnovationNumber();
            nodes.put(nodeInnovationNumber, new NodeGene(nodeInnovationNumber, INPUT));
            nodes.get(nodeInnovationNumber).setLayer(0);
        }

        // generate output nodes
        for (int i = 0; i < amountOfOutputNodes; i++) {
            final int nodeInnovationNumber = newNodeInnovationNumber();
            nodes.put(nodeInnovationNumber, new NodeGene(nodeInnovationNumber, OUTPUT));
            nodes.get(nodeInnovationNumber).setLayer(1);
        }

        connectNeurons();
    }

    /**
     * Connects all of the currently added neurones to one another.
     * Is used during the initial network generation only
     */
    private void connectNeurons() {
        nodes.values().stream().filter(nodeGene -> nodeGene.getLayer() == 0).forEach(inputNode ->
                nodes.values().stream().filter(nodeGene -> nodeGene.getLayer() == 1)
                        .forEach(outputNode ->
                                addNewConnection(inputNode, outputNode, generateRandomWeight())));
    }

    /**
     * Used to generate new connection, add it to the connections pull using new innovation number and then update
     * outgoing connections of the in node gene with this newly created connection
     * Do not confuse with addConnection method that is used during crossover
     */
    private void addNewConnection(final NodeGene in, final NodeGene out, final float weight) {
        final ConnectionGene newConnection = new ConnectionGene(in.getId(), out.getId(), weight, true, newConnectionInnovationNumber());
        connections.put(newConnection.getInnovationNumber(), newConnection);
        in.addOutgoingConnection(newConnection);
    }

    /**
     * Check if the chosen nodes are fit for add connection mutation or not
     * That means that they:
     * - should not be the same node,
     * - should not be on the same layer,
     * - should not be already connected,
     */
    private boolean nodesAreNotGood(final NodeGene node1, final NodeGene node2) {
        final boolean nodesAreTheSame = node1.equals(node2);
        final boolean nodesAreOnTheSameLayer = node1.getLayer() == node2.getLayer();
        final boolean nodesAreAlreadyConnected = node1.isConnectedTo(node2);
        return nodesAreTheSame || nodesAreOnTheSameLayer || nodesAreAlreadyConnected;
    }

    private float generateRandomWeight() {
        return r.nextFloat() * 2f - 1f;
    }
}
