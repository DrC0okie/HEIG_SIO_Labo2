package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.groupF.insertion.FurthestInsert;
import ch.heig.sio.lab2.groupF.insertion.NearestInsert;
import ch.heig.sio.lab2.groupF.statistics.Statistics;
import ch.heig.sio.lab2.groupF.two_opt.TwoOptBestImprovement;
import ch.heig.sio.lab2.tsp.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import static ch.heig.sio.lab2.groupF.statistics.Statistics.*;

/**
 * Effectue une analyse comparative des heuristiques utilisées pour résoudre le TSP.
 * Cette analyse inclut :
 * - L'évaluation des heuristiques de construction de tournées initiales (aléatoires et insertion).
 * - L'amélioration des tournées avec 2-opt.
 * - L'utilisation de multithreading pour paralléliser les évaluations par essai.
 */
public final class Analyze {

    private static final long SEED = 0x134DAE9; // Graine pour générer des résultats reproductibles
    private static final int TRIALS = 50; // Nombre de tests par heuristique
    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    /**
     * Représente un ensemble de données TSP avec un nom, un chemin vers le fichier
     * contenant les données et la longueur optimale de la tournée.
     */
    private record DataSet(String name, String filePath, long optimalLength) {}

    public static void main(String[] args) throws FileNotFoundException {
        // Définir les ensembles de données et leurs longueurs optimales
        List<DataSet> dataSets = Arrays.asList(
                new DataSet("pcb442", "data/pcb442.dat", 50778),
                new DataSet("att532", "data/att532.dat", 86729),
                new DataSet("u574", "data/u574.dat", 36905),
                new DataSet("pcb1173", "data/pcb1173.dat", 56892),
                new DataSet("nrw1379", "data/nrw1379.dat", 56638),
                new DataSet("u1817", "data/u1817.dat", 57201)
        );

        // Heuristique d'amélioration 2-opt
        TspImprovementHeuristic twoOpt = new TwoOptBestImprovement();

        // Analyser chaque ensemble de données
        for (DataSet ds : dataSets) {
            System.out.println("Dataset: " + ds.name() + ", Optimal length: " + ds.optimalLength());

            TspData data = TspData.fromFile(ds.filePath());

            // Générer une liste de villes de départ pour les heuristiques d'insertion
            int[] startCities = getFirstNCities(new RandomTour(SEED).computeTour(data, 0), TRIALS);

            // Évaluer chaque heuristique
            List<Statistics> statistics = new ArrayList<>();
            statistics.add(evaluateHeuristic("Random Tour", data, new RandomTour(SEED), null, twoOpt, ds.optimalLength()));
            statistics.add(evaluateHeuristic("Nearest Insertion", data, new NearestInsert(), startCities, twoOpt, ds.optimalLength()));
            statistics.add(evaluateHeuristic("Furthest Insertion", data, new FurthestInsert(), startCities, twoOpt, ds.optimalLength()));

            // Afficher les résultats des statistiques collectées
            printStatisticsTable(statistics);
            System.out.println();
        }
    }

    /**
     * Évalue une heuristique donnée en utilisant un thread par essai.
     *
     * @param heuristicName Le nom de l'heuristique.
     * @param data Les données du TSP.
     * @param heuristic L'heuristique constructive utilisée pour construire des tournées initiales.
     * @param startCities Les villes de départ pour les heuristiques d'insertion (null pour RandomTour).
     * @param improvement L'heuristique d'amélioration (e.g., 2-opt).
     * @param optimalLength La longueur optimale pour cet ensemble de données.
     * @return Les statistiques collectées pour cette heuristique.
     */
    private static Statistics evaluateHeuristic(
            String heuristicName,
            TspData data,
            TspConstructiveHeuristic heuristic,
            int[] startCities,
            TspImprovementHeuristic improvement,
            long optimalLength) {

        Statistics stats = new Statistics(heuristicName, optimalLength);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<Void>> futures = new ArrayList<>();

        for (int trial = 0; trial < TRIALS; trial++) {
            final int index = trial;
            futures.add(executor.submit(() -> {
                // Construire la tournée initiale
                TspTour initialTour = (startCities != null)
                        ? heuristic.computeTour(data, startCities[index])
                        : heuristic.computeTour(data, 0);

                stats.addInitialLength(initialTour.length());

                // Améliorer la tournée avec l'heuristique d'amélioration
                long startTime = System.nanoTime();
                TspTour improvedTour = improvement.computeTour(initialTour);
                long endTime = System.nanoTime();

                stats.addImprovedLength(improvedTour.length());
                stats.addTime(endTime - startTime);

                return null;
            }));
        }

        // Attendre la fin de toutes les tâches
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Erreur lors de l'exécution de l'évaluation : " + e.getMessage());
            }
        }
        executor.shutdown();

        return stats;
    }

    /**
     * Génère un tableau des premières villes d'une tournée.
     *
     * @param tour La tournée initiale.
     * @param n Le nombre de villes à extraire.
     * @return Un tableau des identifiants des villes.
     */
    public static int[] getFirstNCities(TspTour tour, int n) {
        return tour.tour().stream().limit(n).toArray();
    }
}
