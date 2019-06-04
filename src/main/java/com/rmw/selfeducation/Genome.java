package com.rmw.selfeducation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.rmw.selfeducation.Configuration.INPUT_NEURONS_AMOUNT;
import static com.rmw.selfeducation.Configuration.OUTPUT_NEURONS_AMOUNT;
import static com.rmw.selfeducation.Innovations.newConnectionInnovationNumber;
import static com.rmw.selfeducation.Innovations.newNodeInnovationNumber;
import static com.rmw.selfeducation.NeuronType.BIAS;
import static com.rmw.selfeducation.NeuronType.HIDDEN;
import static com.rmw.selfeducation.NeuronType.INPUT;
import static com.rmw.selfeducation.NeuronType.OUTPUT;

class Genome {

    private final Random r = new Random();
    private final Map<Integer, NodeGene> nodes = new TreeMap<>();
    private final Map<Integer, ConnectionGene> connections = new TreeMap<>();

    private final Map<Integer, List<NodeGene>> nodesByLayer = new TreeMap<>();
    private int amountOfLayers; // currently in the genome


    /**
     * Fresh network always contains:
     * 2 layers of nodes - one for inputs and another one for outputs
     * Among input node a bias neuron is added
     * All input node are connected to the output node with random weight assigned
     */
    Genome() {
        // add bias neuron to the network first
        final NodeGene bias = new NodeGene(newNodeInnovationNumber(), BIAS);
        bias.setLayer(0);
        bias.addSumValue(1f); //bias neuron always has an output value of 1
        nodes.put(bias.getId(), bias);

        // generate input nodes
        for (int i = 0; i < INPUT_NEURONS_AMOUNT; i++) {
            final int nodeInnovationNumber = newNodeInnovationNumber();
            nodes.put(nodeInnovationNumber, new NodeGene(nodeInnovationNumber, INPUT));
            nodes.get(nodeInnovationNumber).setLayer(0);
        }

        // generate output nodes
        for (int i = 0; i < OUTPUT_NEURONS_AMOUNT; i++) {
            final int nodeInnovationNumber = newNodeInnovationNumber();
            nodes.put(nodeInnovationNumber, new NodeGene(nodeInnovationNumber, OUTPUT));
            nodes.get(nodeInnovationNumber).setLayer(1);
        }

        connectNeurons();
        updateAmountOfLayers();
        updateNodesByLayers();
    }

    Map<Integer, NodeGene> getNodes() {
        return nodes;
    }

    Map<Integer, ConnectionGene> getConnections() {
        return connections;
    }

    Map<Integer, List<NodeGene>> getNodesByLayer() {
        return nodesByLayer;
    }

    int getAmountOfLayers() {
        return amountOfLayers;
    }

    /**
     * Used to set inputs for the network
     *
     * @param inputs - map with inputs where key is an innovation number of the neuron
     */
    void setInputs(final Map<Integer, Float> inputs) {
        // reset all neurons except the BIAS one
        nodes.values().forEach(node -> {
            if (node.getType() != BIAS) {
                node.reset();
            }
        });
        // set new values for input neurons
        for (final Map.Entry<Integer, Float> entry : inputs.entrySet()) {
            nodes.get(entry.getKey()).addSumValue(entry.getValue());
        }
    }

    /**
     * Calculates output of the neural network based on the inputs
     *
     * @return list of output values from each output node
     */
    List<Float> feedForward() {
        final List<Float> result = new ArrayList<>(OUTPUT_NEURONS_AMOUNT);
        // go through all of the neurons and feedForward its output to the nodes its connected with
        nodesByLayer.values().forEach(layer ->
                layer.forEach(node ->
                        node.getOutgoingConnections().forEach(outgoingConnection -> {
                            if (outgoingConnection.isExpressed()) {
                                final NodeGene outGene = nodes.get(outgoingConnection.getOutNode());
                                outGene.addSumValue(node.getOutputValue() * outgoingConnection.getWeight());
                            }
                        })
                )
        );
        nodesByLayer.get(amountOfLayers - 1).forEach(outputNeuron -> result.add(outputNeuron.getOutputValue()));
        return result;
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
        // if chosen connection is already disabled - select a new one. Repeat until we get valid connection
        while (!connectionToBeDisabled.isExpressed()) {
            connectionToBeDisabled = connections.get(r.nextInt(connections.size()));
        }

        connectionToBeDisabled.disable();

        final NodeGene newNode = new NodeGene(newNodeInnovationNumber(), HIDDEN);
        final NodeGene disabledConnectionInNode = nodes.get(connectionToBeDisabled.getInNode());
        final NodeGene disabledConnectionOutNode = nodes.get(connectionToBeDisabled.getOutNode());
        final float disabledConnectionWeight = connectionToBeDisabled.getWeight();

        realignLayers(disabledConnectionInNode.getLayer(), disabledConnectionOutNode.getLayer(), newNode);

        nodes.put(newNode.getId(), newNode);
        addNewConnection(disabledConnectionInNode, newNode, 1f);
        addNewConnection(newNode, disabledConnectionOutNode, disabledConnectionWeight);

        updateNodesByLayers();
    }

    /**
     * Used during add node mutation. The main function is to assign a layer to the newly added node.
     * In case the new node is between neighbor nodes we 'squeeze' the new node in by introducing a new layer
     * into the network and moving all the others nodes to the corresponding layer (by increasing the layer number)
     *
     * @param inNodeLayer  - layer of the input node of the disabled connection
     * @param outNodeLayer - layer of the output node of the disabled connection
     * @param newNode      - new node that will be assigned with layer number
     */
    private void realignLayers(final int inNodeLayer, final int outNodeLayer, final NodeGene newNode) {
        final int dif = outNodeLayer - inNodeLayer;
        if (dif == 1) {
            // squeeze new layer in
            final int newLayer = inNodeLayer + 1;
            // move all layers up to fit in the new one
            nodes.values().stream()
                    .filter(node -> node.getLayer() >= newLayer)
                    .forEach(node -> node.setLayer(node.getLayer() + 1));
            newNode.setLayer(newLayer);

            // it is important to update the layers count after we have introduced a new layer
            updateAmountOfLayers();
        } else if (dif == 2) {
            newNode.setLayer(inNodeLayer + 1);
        } else {
            // choose random layer between in and out nodes
            newNode.setLayer(getRandomNumberInRange(inNodeLayer + 1, outNodeLayer - 1));
        }
    }

    /**
     * Connects all of the currently added neurones to one another.
     * Should be used during the initial network generation only
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
     * Checks if the chosen nodes are fit for add connection mutation or not
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

    private void updateAmountOfLayers() {
        amountOfLayers = 0;
        for (final NodeGene nodeGene : nodes.values()) {
            if (nodeGene.getLayer() > amountOfLayers) amountOfLayers = nodeGene.getLayer();
        }
        amountOfLayers++;
    }

    private void updateNodesByLayers() {
        nodesByLayer.clear();
        for (int i = 0; i < amountOfLayers; i++) {
            final int finalI = i;
            final List<NodeGene> nodesOnThisLayer = nodes.values().stream()
                    .filter(nodeGene -> nodeGene.getLayer() == finalI).collect(Collectors.toList());
            nodesByLayer.put(i, nodesOnThisLayer);
        }
    }

    private float generateRandomWeight() {
        return r.nextFloat() * 2f - 1f;
    }

    private int getRandomNumberInRange(final int min, final int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        return r.nextInt((max - min) + 1) + min;
    }

}