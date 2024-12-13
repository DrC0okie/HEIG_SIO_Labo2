package ch.heig.sio.lab2.groupF.statistics;

import java.util.*;

/**
 * Gère les statistiques des heuristiques TSP.
 * - Collecte les longueurs des tournées initiales et optimisées.
 * - Mesure les temps d'exécution pour chaque itération.
 * - Calcule des la longueur minimale, moyenne, maximale, et la médiane.
 * - Affiche les résultats de manière formatée.
 *
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public final class Statistics {
    private final String heuristicName;
    private final long optimalLength;
    private final List<Long> initialLengths = new ArrayList<>();
    private final List<Long> improvedLengths = new ArrayList<>();
    private final List<Long> times = new ArrayList<>();
    private SummaryStatistics initialStats;
    private SummaryStatistics improvedStats;
    private double avgTimeMs;

    public Statistics(String heuristicName, long optimalLength) {
        this.heuristicName = heuristicName;
        this.optimalLength = optimalLength;
    }

    /**
     * Ajoute la longueur d'une tournée initiale à la liste des longueurs.
     *
     * @param length La longueur de la tournée initiale.
     */
    public void addInitialLength(long length) {
        initialLengths.add(length);
    }

    /**
     * Ajoute la longueur d'une tournée optimisée (après amélioration) à la liste des longueurs.
     *
     * @param length La longueur de la tournée optimisée.
     */
    public void addImprovedLength(long length) {
        improvedLengths.add(length);
    }

    /**
     * Ajoute le temps d'exécution d'une itération.
     *
     * @param timeNano Le temps d'exécution en nanosecondes.
     */
    public void addTime(long timeNano) {
        times.add(timeNano);
    }

    /**
     * Calcule toutes les statistiques basées sur les données collectées.
     */
    public void computeStatistics() {
        initialStats = SummaryStatistics.calculate(initialLengths, optimalLength);
        improvedStats = SummaryStatistics.calculate(improvedLengths, optimalLength);
        avgTimeMs = times.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1_000_000.0;
    }

    /**
     * Affiche les statistiques pour cette heuristique.
     */
    public void print() {
        System.out.printf("%-18s | %-9s | %s%n", heuristicName, "Initial", initialStats);
        System.out.printf("%-18s | %-9s | %s%n", "", "2-opt", improvedStats);
        System.out.printf("Avg compute time: %.0f ms%n%n", avgTimeMs);
    }

    /**
     * Affiche un tableau formaté contenant les statistiques de toutes les heuristiques.
     *
     * @param allStats Une liste de statistiques pour chaque heuristique évaluée.
     */
    public static void printStatisticsTable(List<Statistics> allStats) {
        String fromOpt = " (from optimal)";
        System.out.printf("%-18s | %-9s | %18s | %18s | %18s | %8s%n",
                "Heuristic", "Status", "Min" + fromOpt, "Avg" + fromOpt, "Max" + fromOpt, "Median");
        System.out.println("-".repeat(104));

        allStats.forEach(stat -> {
            stat.computeStatistics();
            stat.print();
        });
    }
}
