package com.rmw.selfeducation;

import java.text.MessageFormat;
import java.util.Objects;

class ConnectionGene {

    private int inNode;
    private int outNode;
    private float weight;
    private boolean expressed; //if the connection is enabled or not
    private int innovationNumber;

    ConnectionGene(final int inNode, final int outNode, final float weight, final boolean expressed, final int innovationNumber) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.expressed = expressed;
        this.innovationNumber = innovationNumber;
    }

    int getInNode() {
        return inNode;
    }

    void setInNode(final int inNode) {
        this.inNode = inNode;
    }

    int getOutNode() {
        return outNode;
    }

    void setOutNode(final int outNode) {
        this.outNode = outNode;
    }

    float getWeight() {
        return weight;
    }

    void setWeight(final float weight) {
        this.weight = weight;
    }

    boolean isExpressed() {
        return expressed;
    }

    void disable() {
        expressed = false;
    }

    void enable() {
        expressed = true;
    }

    int getInnovationNumber() {
        return innovationNumber;
    }

    void setInnovationNumber(final int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Innovation {0}. {1} -> {2}. Weight: {3}. Expressed: {4}",
                innovationNumber, inNode, outNode, weight, expressed);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ConnectionGene that = (ConnectionGene) o;
        return inNode == that.inNode &&
                outNode == that.outNode &&
                Float.compare(that.weight, weight) == 0 &&
                expressed == that.expressed &&
                innovationNumber == that.innovationNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inNode, outNode, weight, expressed, innovationNumber);
    }
}
