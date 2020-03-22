package com.rmw.selfeducation;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static com.rmw.selfeducation.Configuration.AMOUNT_OF_PLAYERS_IN_POPULATION;

class Population {

    private final List<Player> players = new ArrayList<>();
    private int currentGeneration = 1;
    private int populationAliveFor; //how many updates the current population is alive

    private Player bestPlayer;
    private int bestScoreEver; // keeps the best score ever played

    private final GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

    /**
     * To avoid situation where some players are stuck (not dead) we introduce the maximum count of updated
     * before the population is evolved.
     */
    private int keepPopulationAliveFor = 250;
    /**
     * This number controls how often the life span of the population is doubled
     */
    private static final int INCREASE_POPULATION_LIFE_SPAN_RATE = 15;

    Population(final PApplet pApplet, final GameScreen gameScreen) {
        for (int i = 0; i < AMOUNT_OF_PLAYERS_IN_POPULATION; i++) {
            final Player player = new Player(pApplet, new Genome(), gameScreen);
            players.add(player);
        }
        bestPlayer = players.get(0);
    }

    void update() {
        final boolean atLeastOnePlayerIsAlive = players.stream().anyMatch(Player::isAlive);
        if (atLeastOnePlayerIsAlive && populationAliveFor < keepPopulationAliveFor) {
            players.forEach(Player::update);
            populationAliveFor++;
        } else {
            geneticAlgorithm.evolve();
            currentGeneration++;
            populationAliveFor = 0;
            if (currentGeneration % INCREASE_POPULATION_LIFE_SPAN_RATE == 0) {
                keepPopulationAliveFor *= 2;
            }
        }
    }


    private class GeneticAlgorithm {

        /**
         * When all players died - run the evolution algorithm to get a new, hopefully better population
         */
        private void evolve() {
            assignBestPlayer();
            clonePlayers();
            crossover();
            mutate();
        }

        private void assignBestPlayer() {
            players.forEach(Player::calculateFitness);
            players.sort(Player::compareTo);
            bestPlayer = players.get(0);
            if (bestPlayer.getFitness() < bestScoreEver) {
                bestScoreEver = bestPlayer.getFitness();
            }
        }

        /**
         * Clones some of the best players as is using skewed exponential distribution
         */
        private void clonePlayers() {

        }

        /**
         * The majority of players are formed as a result of crossover
         */
        private void crossover() {

        }

        /**
         * All crossover players are exposed to mutation
         */
        private void mutate() {

        }


    }


}
