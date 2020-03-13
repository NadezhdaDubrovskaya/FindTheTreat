package com.rmw.selfeducation;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

class NodeGene {

    private final int id;
    private final NeuronType type;
    private final List<ConnectionGene> outputConnections = new ArrayList<>();
    private final PVector position = new PVector();

    private int layer;

    /**
     * Value before activation function is applied to it
     */
    private float sumValue;
    /**
     * Value after the activation function is applied
     * This value is later on passed to other neurons through the connection
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

    void incrementLayer() {
        layer++;
    }

    List<ConnectionGene> getOutgoingConnections() {
        return outputConnections;
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

    PVector getPosition() {
        return position;
    }

    void setPosition(final float x, final float y) {
        position.x = x;
        position.y = y;
    }

    /**
     * Resets both sum and outputValue of the neuron
     */
    void reset() {
        sumValue = 0;
        outputValue = 0;
    }

    /**
     * Is used during feed forward
     * Whenever the sumValue is changed, recalculate the outputValue using the activation function
     *
     * @param value - to be added to the sumValue
     */
    void addSumValue(final float value) {
        sumValue = sumValue + value;
        calculateOutputValue();
    }

    /**
     * Basically the activation function of the neuron
     * TODO: make it possible to select an activation function for each neuron from the list of some sort and adjust the method correspondingly
     */
    private void calculateOutputValue() {
        if (sumValue >= 1) {
            outputValue = 1;
            return;
        }
        if (sumValue <= -1) {
            outputValue = -1;
            return;
        }
        outputValue = 0;
    }

    float getOutputValue() {
        return outputValue;
    }

    @Override
    public String toString() {
        return "NodeGene{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}
