package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.groupF.insertion.FurthestInsert;
import ch.heig.sio.lab2.groupF.insertion.NearestInsert;
import ch.heig.sio.lab2.groupF.statistics.Statistics;
import ch.heig.sio.lab2.groupF.two_opt.TwoOptBestImprovement;
import ch.heig.sio.lab2.tsp.RandomTour;
import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public final class Analyze {

    public static void main(String[] args) {
        // Longueurs optimales pour chaque jeu de données
        Map<String, Long> optimalLengths = Map.of(
                "pcb442", 50778L,
                "att532", 86729L,
                "u574", 36905L,
                "pcb1173", 56892L,
                "nrw1379", 56638L,
                "u1817", 57201L
        );

        // Jeux de données
        String[] datasets = {"pcb442", "att532", "u574", "pcb1173", "nrw1379", "u1817"};

        // Heuristiques
        List<TspConstructiveHeuristic> heuristics = List.of(
                new RandomTour(0x134DAE9),
                new NearestInsert(),
                new FurthestInsert()
        );
        List<String> heuristicNames = List.of("RandomTour", "NearestInsert", "FurthestInsert");

        TwoOptBestImprovement twoOpt = new TwoOptBestImprovement();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (String dataset : datasets) {
            TspData data;
            try {
                data = TspData.fromFile("data/" + dataset + ".dat");
            } catch (Exception e) {
                System.err.println("File \"" + dataset + "\" not found");
                continue;
            }

            long optimalLength = optimalLengths.get(dataset);

            // Lancer les tests pour chaque heuristique en parallèle
            List<Future<Statistics>> futureStats = new ArrayList<>();
            for (int i = 0; i < heuristics.size(); i++) {
                final int index = i; // Capture de la variable pour les threads
                futureStats.add(executor.submit(() ->
                        runTests(heuristicNames.get(index), data, heuristics.get(index), twoOpt, optimalLength)
                ));
            }

            // Collecte des résultats pour ce dataset
            List<Statistics> datasetStats = new ArrayList<>();
            for (Future<Statistics> future : futureStats) {
                try {
                    datasetStats.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error while processing heuristic: " + e.getMessage());
                }
            }

            // Afficher les résultats pour ce dataset
            printStatisticsForDataset(dataset, optimalLength, datasetStats);
        }

        executor.shutdown();
    }

    private static Statistics runTests(String heuristicName, TspData data, TspConstructiveHeuristic initialHeuristic,
                                       TwoOptBestImprovement twoOpt, long optimalLength) {
        Statistics stats = new Statistics(heuristicName, optimalLength);

        IntStream.range(0, 50).parallel().forEach(i -> {
            long startTime = System.nanoTime();

            // Génération de la tournée initiale
            TspTour initialTour = initialHeuristic.computeTour(data, 0);

            // Application de 2-opt
            TspTour optimizedTour = twoOpt.computeTour(initialTour);

            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000; // Convertir en ms

            // Enregistrer les statistiques
            synchronized (stats) { // Assurer la sécurité des threads
                stats.addMeasurement(optimizedTour.length(), executionTime);
            }
        });

        return stats;
    }

    private static void printStatisticsForDataset(String dataset, long optimalLength, List<Statistics> datasetStats) {
        System.out.printf("Dataset: %s, Optimal: %d\n", dataset, optimalLength);
        System.out.printf("%-15s | %-8s | %-8s | %-8s | %-15s | %-10s\n",
                "Heuristic", "Min", "Avg", "Max", "Avg Rel. Error", "Avg Time");
        System.out.println("-----------------------------------------------------------------------");
        for (Statistics stats : datasetStats) {
            System.out.println(stats.toCompactString());
        }
        System.out.println();
    }
}


