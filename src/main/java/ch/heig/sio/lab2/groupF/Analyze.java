package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.groupF.insertion.FurthestInsert;
import ch.heig.sio.lab2.groupF.insertion.NearestInsert;
import ch.heig.sio.lab2.groupF.two_opt.TwoOptBestImprovement;
import ch.heig.sio.lab2.tsp.*;
import ch.heig.sio.lab2.groupF.statistics.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

import static ch.heig.sio.lab2.groupF.statistics.Statistics.*;

/**
 * Effectue une analyse comparative des heuristiques utilisées pour résoudre le TSP
 * Cette analyse inclut :
 * - L'évaluation des heuristiques de construction de tournées initiales (aléatoires et insertion).
 * - L'amélioration des tournées avec 2-opt.
 * - L'utilisation d'un ForkJoinPool pour paralléliser les évaluations par heuristique.
 *
 * @author Timothée Van Hove, Jarod Streckeisen
 */
public final class Analyze {

    private static final long SEED = 0x134DAE9; // Graine utilisée pour générer des résultats reproductibles
    private static final int TRIALS = 50; // Nombre de tests effectués pour chaque heuristique

    /**
     * Représente un ensemble de données TSP avec un nom, un chemin vers le fichier
     * contenant les données et la longueur optimale de la tournée.
     */
    private record DataSet(String name, String filePath, long optimalLength) {
    }

    /**
     * Point d'entrée principal du programme.
     *
     * @param args Les arguments de la ligne de commande (non utilisés ici).
     * @throws FileNotFoundException Si un fichier de données ne peut pas être trouvé.
     */
    public static void main(String[] args) throws FileNotFoundException {

        // Liste des ensembles de données à analyser, incluant leurs longueurs optimales
        List<DataSet> dataSets = Arrays.asList(
                new DataSet("pcb442", "data/pcb442.dat", 50778),
                new DataSet("att532", "data/att532.dat", 86729),
                new DataSet("u574", "data/u574.dat", 36905),
                new DataSet("pcb1173", "data/pcb1173.dat", 56892),
                new DataSet("nrw1379", "data/nrw1379.dat", 56638),
                new DataSet("u1817", "data/u1817.dat", 57201)
        );

        // Heuristique d'amélioration utilisée : 2-opt
        TspImprovementHeuristic twoOpt = new TwoOptBestImprovement();

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool(); // ForkJoinPool par défaut

        for (DataSet ds : dataSets) {
            System.out.println("Dataset: " + ds.name() + ", Optimal length: " + ds.optimalLength());

            TspData data = TspData.fromFile(ds.filePath());

            // Générer une liste de villes de départ pour les heuristiques d'insertion
            int[] startCities = getFirstNCities(new RandomTour(SEED).computeTour(data, 0), TRIALS);

            // Définir les stratégies d'évaluation (heuristiques à tester)
            List<EvaluationStrategy> strategies = List.of(
                    new RandomTourEvaluation(new RandomTour(SEED)),
                    new InsertionHeuristicEvaluation(new NearestInsert(), startCities, "Nearest insertion"),
                    new InsertionHeuristicEvaluation(new FurthestInsert(), startCities, "Furthest insertion")
            );

            // Préallouer un tableau pour stocker les statistiques
            Statistics[] statistics = new Statistics[strategies.size()];

            // Utilisation de ForkJoinPool pour paralléliser l'évaluation
            forkJoinPool.invoke(new RecursiveAction() {
                @Override
                protected void compute() {
                    List<RecursiveAction> subtasks = new ArrayList<>();
                    for (int i = 0; i < strategies.size(); i++) {
                        final int index = i;
                        subtasks.add(new RecursiveAction() {
                            @Override
                            protected void compute() {
                                statistics[index] = strategies.get(index).evaluate(data, twoOpt, ds.optimalLength(), TRIALS);
                            }
                        });
                    }
                    invokeAll(subtasks); // Exécute toutes les sous-tâches
                }
            });

            // Afficher les résultats des statistiques collectées
            printStatisticsTable(Arrays.asList(statistics));
            System.out.println();
        }
    }
}
