package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.groupF.insertion.FurthestInsert;
import ch.heig.sio.lab2.groupF.insertion.NearestInsert;
import ch.heig.sio.lab2.groupF.two_opt.TwoOptBestImprovement;
import ch.heig.sio.lab2.tsp.*;
import ch.heig.sio.lab2.groupF.statistics.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static ch.heig.sio.lab2.groupF.statistics.Statistics.*;

public final class Analyze {

    private static final long SEED = 0x134DAE9;
    private static final int TRIALS = 50;

    /**
     * Represents a TSP data set with its name, file path, and optimal length.
     */
    private record DataSet(String name, String filePath, long optimalLength) {}

    /**
     * Main entry point of the Analyze application.
     *
     * @param args Command-line arguments (not used).
     * @throws FileNotFoundException if a dataset file cannot be found
     */
    public static void main(String[] args) throws FileNotFoundException {
        //TODO To delete
        long startTime = System.nanoTime();


        // List of data sets to analyze with their optimal lengths
        List<DataSet> dataSets = Arrays.asList(
                new DataSet("pcb442", "data/pcb442.dat", 50778),
                new DataSet("att532", "data/att532.dat", 86729),
                new DataSet("u574", "data/u574.dat", 36905),
                new DataSet("pcb1173", "data/pcb1173.dat", 56892),
                new DataSet("nrw1379", "data/nrw1379.dat", 56638),
                new DataSet("u1817", "data/u1817.dat", 57201)
        );

        TspImprovementHeuristic twoOpt = new TwoOptBestImprovement();

        for (DataSet ds : dataSets) {
            System.out.println("Dataset: " + ds.name() + ", Optimal length: " + ds.optimalLength());

            TspData data = TspData.fromFile(ds.filePath());

            int[] startCities = getFirstNCities(new RandomTour(SEED).computeTour(data, 0), TRIALS);

            // Define strategies (heuristics)
            List<EvaluationStrategy> strategies = List.of(
                    new RandomTourEvaluation(new RandomTour(SEED)),
                    new InsertionHeuristicEvaluation(new NearestInsert(), startCities, "Nearest insertion"),
                    new InsertionHeuristicEvaluation(new FurthestInsert(), startCities, "Furthest insertion")
            );

            // Thread-safe list to collect statistics
            List<Statistics> statistics = new CopyOnWriteArrayList<>();

            // Parallelize strategy evaluation
            strategies.parallelStream().forEach(strategy -> {
                Statistics stat = strategy.evaluate(data, twoOpt, ds.optimalLength(), TRIALS);
                statistics.add(stat);
            });

            printStatisticsTable(statistics);
            System.out.println();
        }
        
    }
}
