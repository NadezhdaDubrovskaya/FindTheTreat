package com.rmw.selfeducation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.rmw.selfeducation.Innovations.newConnectionInnovationNumber;
import static com.rmw.selfeducation.Innovations.newNodeInnovationNumber;
import static com.rmw.selfeducation.NeuronType.BIAS;
import static com.rmw.selfeducation.NeuronType.HIDDEN;

class Genome {

    /*
     * It is important to keep nodes and connections in accordance to their innovation number.
     * That is why we are using TreeMap and Innovation number as a key
     */
    private final Map<Integer, NodeGene> nodes = new TreeMap<>();
    private final Map<Integer, ConnectionGene> connections = new TreeMap<>();

    private final Map<Integer, List<NodeGene>> nodesByLayer = new TreeMap<>();
    /**
     * The amount of layers currently in the genome.
     * We need to keep track of it for the correct neural network behaviour
     */
    private int amountOfLayers; // currently in the genome

    void firstInitialization() {
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

    void setAmountOfLayers(final int amountOfLayers) {
        this.amountOfLayers = amountOfLayers;
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
        for (final Map.Entry<Integer, Float> input : inputs.entrySet()) {
            nodes.get(input.getKey()).addSumValue(input.getValue());
        }
    }

    /**
     * Calculates output of the neural network based on the inputs
     *
     * @return list of output values from each output node
     */
    List<Float> feedForward() {

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
        return nodesByLayer.get(amountOfLayers - 1).stream()
                .map(NodeGene::getOutputValue)
                .collect(Collectors.toList());
    }

    /**
     * Mutation during which currently missing connection is added.
     * The connection can be only in the "left-to-right" direction and between different
     * nodes located on the different layers
     * If the network is fully connected, method does nothing
     */
    void addConnectionMutation() {
        if (networkFullyConnected()) {
            return;
        }

        NodeGene node1 = nodes.get(Utils.getRandomInt(nodes.size()));
        NodeGene node2 = nodes.get(Utils.getRandomInt(nodes.size()));
        while (nodesAreNotGood(node1, node2)) {
            node1 = nodes.get(Utils.getRandomInt(nodes.size()));
            node2 = nodes.get(Utils.getRandomInt(nodes.size()));
        }

        // checks if nodes have to be swapped since only the "left-to-right" connections are allowed
        if (node1.getLayer() > node2.getLayer()) {
            addNewConnection(node2, node1, Utils.generateRandomWeight());
        } else {
            addNewConnection(node1, node2, Utils.generateRandomWeight());
        }
    }

    /**
     * Mutation during which a new node is added.
     * <p>
     * The node is added according to the following algorithm:
     * - Choose random connection and disable it
     * - Add a new node to the list of nodes with new node innovation number
     * - Create two new connections:
     * 1. From in node of the disabled connection to new node with weight equal to 1
     * 2. From new node to out node of the disable connection with disabled connection weight
     */
    void addNodeMutation() {
        ConnectionGene connectionToBeDisabled = connections.get(Utils.getRandomInt(connections.size() - 1));
        // if chosen connection is already disabled - select a new one. Repeat until we get valid connection
        while (!connectionToBeDisabled.isExpressed()) {
            connectionToBeDisabled = connections.get(Utils.getRandomInt(connections.size() - 1));
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

    void updateNodesByLayers() {
        nodesByLayer.clear();
        for (int i = 0; i < amountOfLayers; i++) {
            final int finalI = i;
            final List<NodeGene> nodesOnThisLayer = nodes.values().stream()
                    .filter(nodeGene -> nodeGene.getLayer() == finalI)
                    .collect(Collectors.toList());
            nodesByLayer.put(i, nodesOnThisLayer);
        }
    }

    /**
     * Checks if network is already fully connected (all nodes are connected with one another)
     *
     * @return - true if there is no place for a new connection
     */
    private boolean networkFullyConnected() {
        for (final List<NodeGene> nodeOnLayer : nodesByLayer.values()) {
            final int currentLayer = nodeOnLayer.get(0).getLayer();
            // get all nodes from subsequent layers
            final List<Integer> allNodesOnSubsequentLayers = nodes.values().stream()
                    .filter(nodeGene -> nodeGene.getLayer() > currentLayer)
                    .map(NodeGene::getId)
                    .collect(Collectors.toList());
            // go through all nodes on this layer and check whether
            // it is connected to all nodes from the subsequent layers
            for (final NodeGene node : nodeOnLayer) {
                final List<Integer> outNodes = node.getOutgoingConnections().stream()
                        .map(ConnectionGene::getOutNode).collect(Collectors.toList());
                if (!outNodes.containsAll(allNodesOnSubsequentLayers)) {
                    return false;
                }
            }
        }
        return true;
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
            newNode.setLayer(newLayer);

            /*
             * Move all layers up to fit in the new one.
             * Note that newNode isn't added to the nodes list yet thus we can safely use a foreach
             * for all nodes on the newLayer and higher
             */
            nodes.values().stream()
                    .filter(node -> node.getLayer() >= newLayer)
                    .forEach(NodeGene::incrementLayer);

            // it is important to update the layers count after we have introduced a new layer
            updateAmountOfLayers();
        } else if (dif == 2) {
            newNode.setLayer(inNodeLayer + 1);
        } else {
            // choose random layer between in and out nodes
            newNode.setLayer(Utils.getRandomNumberInRange(inNodeLayer + 1, outNodeLayer - 1));
        }
    }

    /**
     * Connects all of the currently added neurones to one another.
     * Should be used during the initial network generation only.
     * <p>
     * Each node on layer 0 connects to every node on layer 1 generating random weight for this connection
     */
    private void connectNeurons() {
        nodes.values().stream().filter(nodeGene -> nodeGene.getLayer() == 0)
                .forEach(inputNode -> nodes.values().stream().filter(nodeGene -> nodeGene.getLayer() == 1)
                        .forEach(outputNode -> addNewConnection(inputNode, outputNode, Utils.generateRandomWeight())));
    }

    /**
     * Used to generate new connection, add it to the connections pull using new innovation number and then update
     * outgoing connections of the in node gene with this newly created connection
     */
    private void addNewConnection(final NodeGene in, final NodeGene out, final float weight) {
        final int innovationNumber = newConnectionInnovationNumber();
        final ConnectionGene newConnection = new ConnectionGene(in.getId(), out.getId(), weight, true, innovationNumber);
        connections.put(innovationNumber, newConnection);
        in.addOutgoingConnection(newConnection);
    }

    /**
     * Checks if the chosen nodes are fit for add connection mutation or not
     * <p>
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Genome genome = (Genome) o;
        return Objects.equals(nodes, genome.nodes) &&
                Objects.equals(connections, genome.connections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, connections);
    }
}