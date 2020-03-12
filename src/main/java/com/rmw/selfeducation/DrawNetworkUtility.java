package com.rmw.selfeducation;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rmw.selfeducation.Configuration.ANN_HEIGHT;
import static com.rmw.selfeducation.Configuration.ANN_WIDTH;
import static com.rmw.selfeducation.Configuration.BLACK;
import static com.rmw.selfeducation.Configuration.BLUE;
import static com.rmw.selfeducation.Configuration.NODE_SIZE;
import static com.rmw.selfeducation.Configuration.RED;
import static com.rmw.selfeducation.Configuration.START_X;
import static com.rmw.selfeducation.Configuration.START_Y;
import static com.rmw.selfeducation.Configuration.WHITE;
import static com.rmw.selfeducation.NeuronType.BIAS;
import static processing.core.PConstants.CENTER;

/**
 * This class is for the graphical representation of the ANN on the screen
 */
class DrawNetworkUtility {

    private final PApplet pApplet;

    DrawNetworkUtility(final PApplet pApplet) {
        this.pApplet = pApplet;
    }

    /**
     * Draws all of the nodes and the connections to graphically represent the network
     * Note that the nodes within the network are updated with the new coordinates
     *
     * @param genome - the genome which should be displayed.
     */
    void draw(final Genome genome) {
        updateNodeCoordinates(genome);
        drawConnections(genome);
        drawNodes(genome);
    }

    private void updateNodeCoordinates(final Genome genome) {
        final float layerWidth = ANN_WIDTH / genome.getAmountOfLayers();
        final List<Float> layerStartingCoordinates = getStartingCoordinatesArray(genome.getAmountOfLayers(), layerWidth, START_X);

        for (final Map.Entry<Integer, List<NodeGene>> entry : genome.getNodesByLayer().entrySet()) {
            final int layer = entry.getKey();
            final List<NodeGene> nodesInLayer = entry.getValue();
            final int amountOfNodes = nodesInLayer.size();
            final float rowHeight = ANN_HEIGHT / amountOfNodes;
            final List<Float> rowStartingCoordinates = getStartingCoordinatesArray(amountOfNodes, rowHeight, START_Y);

            for (int i = 0; i < amountOfNodes; i++) {
                final float x = layerStartingCoordinates.get(layer) + layerWidth / 2;
                final float y = rowStartingCoordinates.get(i) + rowHeight / 2;
                nodesInLayer.get(i).setPosition(x, y);
            }
        }
    }

    private void drawConnections(final Genome genome) {
        genome.getConnections().values().forEach(connection -> {
            if (connection.isExpressed()) {
                final PVector startCoordinates = genome.getNodes().get(connection.getInNode()).getPosition();
                final PVector endCoordinated = genome.getNodes().get(connection.getOutNode()).getPosition();
                pApplet.strokeWeight(Math.abs(connection.getWeight()));
                if (connection.getWeight() > 0) {
                    pApplet.stroke(RED.v1, RED.v2, RED.v3);
                } else {
                    pApplet.stroke(BLUE.v1, BLUE.v2, BLUE.v3);
                }
                pApplet.line(startCoordinates.x, startCoordinates.y, endCoordinated.x, endCoordinated.y);
            }
        });
        // reset settings for the next processing graphical object
        pApplet.stroke(BLACK.v1, BLACK.v2, BLACK.v3);
        pApplet.strokeWeight(0.5f);
    }

    private void drawNodes(final Genome genome) {
        genome.getNodes().values().forEach(node -> {
            final PVector coordinates = node.getPosition();
            if (node.getType() == BIAS) {
                pApplet.fill(RED.v1, RED.v2, RED.v3);
            } else {
                pApplet.fill(BLACK.v1, BLACK.v2, BLACK.v3);
            }
            pApplet.ellipse(coordinates.x, coordinates.y, NODE_SIZE, NODE_SIZE);
            pApplet.fill(WHITE.v1, WHITE.v2, WHITE.v3);
            pApplet.textAlign(CENTER, CENTER);
            pApplet.text(node.getId(), coordinates.x, coordinates.y);
        });
    }

    /**
     * Is used to get an array that contains the starting coordinates of each row/column to display an ANN
     * <p>
     * For example, if we have 3 layers in the network, we want to know how to display
     * them evenly on the that starts at 0 and has width of 10.
     * The output array will contain the starting coordinates of each layer that is [0, 3.33, 6.66]
     *
     * @param arraySize       - amount of layers or nodes in the layer
     * @param widthOrHeight   - already calculated width of the layer or height of row
     * @param startCoordinate - starting x or y coordinate of the plane where the ANN should be displayed
     * @return array with starting coordinates of each row/column to display an ANN
     */
    private List<Float> getStartingCoordinatesArray(final int arraySize, final float widthOrHeight, final float startCoordinate) {
        final List<Float> result = new ArrayList<>(arraySize);
        for (int i = 0; i < widthOrHeight; i++) {
            result.add(i * widthOrHeight + startCoordinate);
        }
        return result;
    }

}
