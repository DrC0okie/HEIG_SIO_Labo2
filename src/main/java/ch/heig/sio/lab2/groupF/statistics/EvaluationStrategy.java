package ch.heig.sio.lab2.groupF.statistics;

import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspImprovementHeuristic;

public interface EvaluationStrategy {
    Statistics evaluate(
            TspData data,
            TspImprovementHeuristic improvement,
            long optimalLength,
            int trials);
}
