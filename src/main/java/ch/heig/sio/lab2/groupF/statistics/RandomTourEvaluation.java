package ch.heig.sio.lab2.groupF.statistics;

import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

public class RandomTourEvaluation extends BaseEvaluationStrategy {
    private final TspConstructiveHeuristic randomTour;

    public RandomTourEvaluation(TspConstructiveHeuristic randomTour) {
        super("Random Tour");
        this.randomTour = randomTour;
    }

    @Override
    protected TspTour constructInitialTour(TspData data, int trialIndex) {
        return randomTour.computeTour(data, 0);
    }
}

