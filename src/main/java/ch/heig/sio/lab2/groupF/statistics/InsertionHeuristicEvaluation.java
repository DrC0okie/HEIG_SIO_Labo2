package ch.heig.sio.lab2.groupF.statistics;

import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

public class InsertionHeuristicEvaluation extends BaseEvaluationStrategy {
    private final TspConstructiveHeuristic heuristic;
    private final int[] startCities;

    public InsertionHeuristicEvaluation(TspConstructiveHeuristic heuristic, int[] startCities, String name) {
        super(name);
        this.heuristic = heuristic;
        this.startCities = startCities;
    }

    @Override
    protected TspTour constructInitialTour(TspData data, int trialIndex) {
        int startCity = startCities[trialIndex];
        return heuristic.computeTour(data, startCity);
    }
}

