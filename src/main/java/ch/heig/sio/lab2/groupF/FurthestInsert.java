package ch.heig.sio.lab2.groupF;

import java.util.Comparator;

/**
 * Implémente l'heuristique d'insertion de la ville la plus éloignée pour le TSP.
 * Cette heuristique sélectionne la ville non visitée qui est la plus éloignée d'une ville déjà dans la tournée.
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public class FurthestInsert extends DistanceBasedInsert {

    /**
     * Renvoie le comparateur pour trouver la ville avec la plus grande distance (plus éloignée).
     * @return Un comparateur qui favorise les distances les plus grandes.
     */
    @Override
    protected Comparator<Long> getDistanceComparator() {
        return Comparator.reverseOrder(); // Distance maximale
    }
}
