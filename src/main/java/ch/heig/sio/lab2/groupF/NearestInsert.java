package ch.heig.sio.lab2.groupF;

import java.util.Comparator;

/**
 * Implémente l'heuristique d'insertion de la ville la plus proche pour le TSP.
 * Cette heuristique sélectionne la ville non visitée qui est la plus proche d'une ville déjà dans la tournée.
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public class NearestInsert extends DistanceBasedInsert {

    /**
     * Renvoie le comparateur pour trouver la ville avec la plus petite distance (plus proche).
     * @return Un comparateur qui favorise les distances les plus petites.
     */
    @Override
    protected Comparator<Long> getDistanceComparator() {
        return Comparator.naturalOrder(); // Distance minimale
    }
}
