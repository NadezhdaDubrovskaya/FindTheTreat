package com.rmw.selfeducation;

import processing.core.PApplet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.AMOUNT_OF_PLAYERS_IN_POPULATION;

class Population {

    private final List<Player> players = new ArrayList<>();
    private int currentGeneration = 1;
    private int bestScoreSoFar = 0; // keeps the best score ever played
    private Player bestPlayer;
    private int populationAliveFor = 0; //how many updates the current population is alive

    Population(final PApplet pApplet, final GameScreen gameScreen) {
        for (int i = 0; i < AMOUNT_OF_PLAYERS_IN_POPULATION; i++) {
            final Player player = new Player(pApplet, new Genome(), gameScreen);
            players.add(player);
        }
    }

    void update() {
        final boolean atLeastOnePlayerIsAlive = players.stream().anyMatch(Player::isAlive);
        if (atLeastOnePlayerIsAlive) {
            players.forEach(Player::update);
            populationAliveFor++;
        } else {
            evolve();
        }
    }

    /**
     * When all players died - run the evolution algorithm to get new population
     */
    private void evolve() {
        throw new NotImplementedException();
    }


}
