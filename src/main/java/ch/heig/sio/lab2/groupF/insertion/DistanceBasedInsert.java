package ch.heig.sio.lab2.groupF.insertion;

import java.util.Comparator;
import java.util.List;

/**
 * Implémente les parties communes des heuristiques d'insertion basées sur la distance pour le TSP
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public abstract class DistanceBasedInsert extends BaseInsertionHeuristic {

    /**
     * Définit la fonction de comparaison utilisée pour choisir la ville à insérer.
     * Les sous-classes doivent implémenter cette méthode pour fournir une stratégie (plus proche ou plus éloignée).
     * @return Un comparateur définissant comment comparer les distances.
     */
    protected abstract Comparator<Long> getDistanceComparator();

    /**
     * Sélectionne la ville non visitée selon une stratégie basée sur la distance à la tournée actuelle
     * (plus proche ou plus éloignée).
     * @param minDistances    Tableau des distances minimales entre les villes non visitées et la tournée.
     * @param unvisitedCities La liste des villes restantes à insérer.
     * @return L'index de la ville à insérer selon la stratégie définie.
     */
    @Override
    protected int selectCityToInsert(long[] minDistances, List<Integer> unvisitedCities) {
        int selectedCity = -1;
        Comparator<Long> comparator = getDistanceComparator();
        long optimalDistance = comparator == Comparator.naturalOrder() ? Long.MAX_VALUE : Long.MIN_VALUE;

        // Parcourt toutes les villes non visitées.
        for (int city : unvisitedCities) {
            long distanceToTour = minDistances[city];  // Utilise la distance mémorisée.

            // Mise à jour si la distance correspond à la stratégie (plus petite ou plus grande).
            if (comparator.compare(distanceToTour, optimalDistance) < 0) {
                optimalDistance = distanceToTour;
                selectedCity = city;
            }
        }

        // Vérifie qu'une ville valide a été trouvée.
        if (selectedCity == -1) {
            throw new IllegalStateException("Aucune ville valide trouvée pour l'insertion.");
        }

        // Retirer la ville sélectionnée de la liste des villes non visitées.
        unvisitedCities.remove((Integer) selectedCity);
        return selectedCity;

    }
}
