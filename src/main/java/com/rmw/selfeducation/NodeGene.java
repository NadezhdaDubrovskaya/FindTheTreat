package com.rmw.selfeducation;

import java.util.ArrayList;
import java.util.List;

class NodeGene {

    private final int id;
    private final NeuronType type;
    private final List<ConnectionGene> outputConnections = new ArrayList<>();

    private int layer;

    /**
     * Value before activation function is applied to it
     */
    private float sumValue;
    /**
     * Value after the activation function that is later on passed to other neurons through the connection
     */
    private float outputValue;

    NodeGene(final int id, final NeuronType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Checks if the node is already connected to the passed node
     */
    boolean isConnectedTo(final NodeGene node) {
        if (node.getLayer() < layer) {
            return node.outputConnections.stream().anyMatch(connectionGene -> connectionGene.getOutNode() == id);
        } else {
            return outputConnections.stream().anyMatch(connectionGene -> connectionGene.getOutNode() == node.id);
        }
    }

    int getLayer() {
        return layer;
    }

    void setLayer(final int layer) {
        this.layer = layer;
    }

    void addOutgoingConnection(final ConnectionGene connection) {
        outputConnections.add(connection);
    }

    int getId() {
        return id;
    }

    NeuronType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "NodeGene{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}
