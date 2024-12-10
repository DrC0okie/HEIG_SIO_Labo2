package ch.heig.sio.lab2.groupF.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe pour gérer les statistiques sur les longueurs de tournées et les temps d'exécution.
 */
public class Statistics {
    private final String heuristicName;
    private final long optimalLength;
    private final List<Long> tourLengths;
    private final List<Long> executionTimes;

    public Statistics(String heuristicName, long optimalLength) {
        this.heuristicName = heuristicName;
        this.optimalLength = optimalLength;
        this.tourLengths = new ArrayList<>();
        this.executionTimes = new ArrayList<>();
    }

    /**
     * Ajoute une mesure de longueur et de temps d'exécution.
     *
     * @param tourLength     Longueur de la tournée.
     * @param executionTime  Temps d'exécution en millisecondes.
     */
    public void addMeasurement(long tourLength, long executionTime) {
        tourLengths.add(tourLength);
        executionTimes.add(executionTime);
    }

    /**
     * Retourne la longueur minimale des tournées.
     */
    public long getMinLength() {
        return tourLengths.stream().min(Long::compare).orElse(0L);
    }

    /**
     * Retourne la longueur maximale des tournées.
     */
    public long getMaxLength() {
        return tourLengths.stream().max(Long::compare).orElse(0L);
    }

    /**
     * Retourne la longueur moyenne des tournées.
     */
    public double getAverageLength() {
        return tourLengths.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    /**
     * Retourne le temps moyen d'exécution.
     */
    public double getAverageExecutionTime() {
        return executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    /**
     * Retourne le pourcentage d'écart moyen par rapport à la longueur optimale.
     */
    public double getAverageRelativeError() {
        return (getAverageLength() - optimalLength) / (double) optimalLength * 100;
    }

    /**
     * Retourne un format compact des statistiques pour affichage dans un tableau.
     */
    public String toCompactString() {
        return String.format("%-15s | %6d | %9.2f | %6d | %14.2f%% | %9.2f ms",
                heuristicName, getMinLength(), getAverageLength(), getMaxLength(),
                getAverageRelativeError(), getAverageExecutionTime());
    }
}
