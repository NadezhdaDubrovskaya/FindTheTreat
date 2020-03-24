package com.rmw.selfeducation;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.rmw.selfeducation.Configuration.GENOCIDE_PERCENTAGE;
import static com.rmw.selfeducation.Configuration.POPULATION_SIZE;

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
        for (int i = 0; i < POPULATION_SIZE; i++) {
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
            killTheWeakOnes();
            breedTheSurvivors();
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
         * Randomly kills the majority of the population as during the natural evolution.
         * The more fit the player is (lesser fitness number) the higher chances they have to survive this genocide :)
         */
        private void killTheWeakOnes() {
            final int summaryFitness = calculateSummaryFitness();
            final HashSet<Player> annihilationList = new HashSet<>();
            final int amountToKill = Math.round(POPULATION_SIZE * GENOCIDE_PERCENTAGE);
            while (annihilationList.size() < amountToKill) {
                final int rand = Utils.getRandomInt(summaryFitness);
                int runningSum = 0;
                /*
                 * I do not really get it how this works exactly but the idea is that the player with higher score is
                 * more likely to be chosen for the genocide and this is exactly what we need - to kill off the weak ones
                 */
                for (final Player player : players) {
                    runningSum += player.getFitness();
                    if (runningSum > rand) {
                        annihilationList.add(player);
                        break;
                    }
                }
            }
            players.removeAll(annihilationList);

            /*
             * We might have killed the best player (low chances and yet...)
             * so we bring him back into the population if necessary
             */

        }

        private int calculateSummaryFitness() {
            int summaryFitness = 0;
            for (final Player player : players) {
                summaryFitness += player.getFitness();
            }
            return summaryFitness;
        }

        /**
         * The majority of players are formed as a result of breeding the survivals using crossover
         */
        private void breedTheSurvivors() {

        }

        /**
         * All crossover players are exposed to mutation
         */
        private void mutate() {

        }


    }


}
