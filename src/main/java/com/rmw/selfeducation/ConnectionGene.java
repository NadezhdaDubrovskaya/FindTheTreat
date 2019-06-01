package com.rmw.selfeducation;

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
}
