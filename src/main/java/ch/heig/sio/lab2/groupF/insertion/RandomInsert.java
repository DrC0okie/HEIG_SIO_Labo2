package ch.heig.sio.lab2.groupF.insertion;

import java.util.*;

/**
 * Implémente l'heuristique d'insertion aléatoire pour le problème TSP.
 * Dans cette heuristique, à chaque étape, une ville non visitée est sélectionnée aléatoirement pour être insérée.
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public class RandomInsert extends BaseInsertionHeuristic {

    private static final long SEED = 0x134DA73;
    private final Random random;

    /**
     * Initialise le générateur de nombres pseudo-aléatoires avec la graine définie.
     */
    public RandomInsert() {
        this.random = new Random(SEED);
    }

    @Override
    protected int selectCityToInsert(long[] minDistances, List<Integer> unvisitedCities) {
        int nextCityIndex = random.nextInt(unvisitedCities.size());
        return unvisitedCities.remove(nextCityIndex);
    }
}

