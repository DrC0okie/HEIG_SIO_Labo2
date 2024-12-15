package ch.heig.sio.lab2.groupF.statistics;

import java.util.Collections;
import java.util.List;

/**
 * Calcule et stocke les statistiques résumées sur les longueurs de tournées.
 * - La longueur minimale, maximale, moyenne et la médiane.
 * - Les erreurs relatives (en pourcentage) par rapport à la longueur optimale.
 *
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public final class SummaryStatistics {
    private final long min, max;
    private final double avg, median, relativeErrorMin, relativeErrorAvg, relativeErrorMax;

    /**
     * Constructeur privé pour initialiser les statistiques résumées.
     * Utiliser la méthode calculate pour créer une instance.
     *
     * @param min              La longueur minimale.
     * @param avg              La longueur moyenne.
     * @param max              La longueur maximale.
     * @param median           La médiane des longueurs.
     * @param relativeErrorMin L'erreur relative pour la longueur minimale.
     * @param relativeErrorAvg L'erreur relative pour la longueur moyenne.
     * @param relativeErrorMax L'erreur relative pour la longueur maximale.
     */
    private SummaryStatistics(long min, double avg, long max, double median,
                              double relativeErrorMin, double relativeErrorAvg, double relativeErrorMax) {
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.median = median;
        this.relativeErrorMin = relativeErrorMin;
        this.relativeErrorAvg = relativeErrorAvg;
        this.relativeErrorMax = relativeErrorMax;
    }

    /**
     * Calcule les statistiques résumées
     *
     * @param values        La liste des longueurs de tournées.
     * @param optimalLength La longueur optimale de la tournée.
     * @return Une instance de SummaryStatistics contenant les résultats calculés.
     */
    public static SummaryStatistics calculate(List<Long> values, long optimalLength) {
        if (values.isEmpty()) {
            return new SummaryStatistics(0, 0, 0, 0, 0, 0, 0);
        }

        Collections.sort(values);
        long min = values.getFirst();
        long max = values.getLast();
        double avg = values.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
        double median = values.size() % 2 == 0
                ? (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2.0
                : values.get(values.size() / 2);

        // Calcul des erreurs relatives
        double relativeErrorMin = 100.0 * (min - optimalLength) / optimalLength;
        double relativeErrorAvg = 100.0 * (avg - optimalLength) / optimalLength;
        double relativeErrorMax = 100.0 * (max - optimalLength) / optimalLength;

        return new SummaryStatistics(min, avg, max, median, relativeErrorMin, relativeErrorAvg, relativeErrorMax);
    }

    /**
     * @return Une chaîne formatée représentant les statistiques.
     */
    @Override
    public String toString() {
        return String.format("%7d %10s | %7.0f %10s | %7d %10s | %8.0f",
                min, formatPercent(relativeErrorMin),
                avg, formatPercent(relativeErrorAvg),
                max, formatPercent(relativeErrorMax),
                median);
    }

    /**
     * @param percent Le pourcentage à formater.
     * @return Une chaîne formatée représentant le pourcentage, ex. "(10.00%)".
     */
    private static String formatPercent(double percent) {
        return String.format("(%.2f%%)", percent);
    }
}
