package ch.heig.sio.lab2.groupF.statistics;

import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspImprovementHeuristic;
import ch.heig.sio.lab2.tsp.TspTour;

public abstract class BaseEvaluationStrategy implements EvaluationStrategy {
    private final String name;

    protected BaseEvaluationStrategy(String name) {
        this.name = name;
    }

    @Override
    public Statistics evaluate(
            TspData data,
            TspImprovementHeuristic improvement,
            long optimalLength,
            int trials) {

        Statistics stats = new Statistics(name, optimalLength);

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();

            // Appel abstrait pour construire le tour initial
            TspTour initialTour = constructInitialTour(data, i);
            stats.addInitialLength(initialTour.length());

            // Amélioration du tour avec l'heuristique 2-opt
            TspTour improvedTour = improvement.computeTour(initialTour);
            stats.addImprovedLength(improvedTour.length());

            long totalTime = System.nanoTime() - startTime;
            stats.addTime(totalTime);
        }

        return stats;
    }

    /**
     * Méthode abstraite pour construire le tour initial. Doit être implémentée par chaque stratégie spécifique.
     */
    protected abstract TspTour constructInitialTour(TspData data, int trialIndex);
}
