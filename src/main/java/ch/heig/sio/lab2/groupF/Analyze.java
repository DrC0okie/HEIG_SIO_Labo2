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
        RandomTour randomTour = new RandomTour(0x134DAE9L);
        NearestInsert nearestInsert = new NearestInsert();
        FurthestInsert furthestInsert = new FurthestInsert();
        TwoOptBestImprovement twoOpt = new TwoOptBestImprovement();

        for (String dataset : datasets) {
            TspData data;
            try {
                data = TspData.fromFile("data/" + dataset + ".dat");
            } catch (Exception e) {
                System.err.println("File \"" + dataset + "\" not found");
                continue;
            }

            long optimalLength = optimalLengths.get(dataset);

            // Stockage des statistiques pour ce dataset
            List<Statistics> datasetStats = new ArrayList<>();

            // Exécution pour chaque heuristique
            datasetStats.add(runTests("RandomTour", data, randomTour, twoOpt, optimalLength));
            datasetStats.add(runTests("NearestInsert", data, nearestInsert, twoOpt, optimalLength));
            datasetStats.add(runTests("FurthestInsert", data, furthestInsert, twoOpt, optimalLength));

            // Afficher les statistiques pour ce dataset
            printStatisticsForDataset(dataset, optimalLength, datasetStats);
        }
    }

    private static Statistics runTests(String heuristicName, TspData data, TspConstructiveHeuristic initialHeuristic,
                                       TwoOptBestImprovement twoOpt, long optimalLength) {
        Statistics stats = new Statistics(heuristicName, data.getNumberOfCities(), optimalLength);

        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();

            // Génération de la tournée initiale
            TspTour initialTour = initialHeuristic.computeTour(data, 0);

            // Application de 2-opt
            TspTour optimizedTour = twoOpt.computeTour(initialTour);

            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000; // Convertir en ms

            // Enregistrer les statistiques
            stats.addMeasurement(optimizedTour.length(), executionTime);
        }

        return stats;
    }

    private static void printStatisticsForDataset(String dataset, long optimalLength, List<Statistics> datasetStats) {
        System.out.printf("Dataset: %s, Optimal: %d\n", dataset, optimalLength);
        System.out.printf("%-15s | %-8s | %-8s | %-8s | %-15s | %-10s\n",
                "Heuristic", "Min", "Avg", "Max", "Avg Rel. Error", "Avg Time");
        System.out.println("-----------------------------------------------------------------------");
        for (Statistics stats : datasetStats) {
            System.out.printf("%-15s | %-8d | %-8.2f | %-8d | %-15.2f%% | %-10.2f ms\n",
                    stats.getHeuristicName(),
                    stats.getMinLength(),
                    stats.getAverageLength(),
                    stats.getMaxLength(),
                    stats.getAverageRelativeError(),
                    stats.getAverageExecutionTime());
        }
        System.out.println();
    }
}
