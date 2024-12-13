package ch.heig.sio.lab2.groupF.two_opt;

import ch.heig.sio.lab2.tsp.TspData;

/**
 * Classe utilitaire pour les opérations de 2-opt.
 *
 * @author Jarod Streckeisen, Timothée Van Hove
 */
final class TwoOptUtils {
    private TwoOptUtils() {
    }

    /**
     * Calcule le gain d'un 2-échange pour les indices donnés dans une tournée.
     *
     * @param extendedTour Le tableau représentant la tournée étendue (circularité incluse).
     * @param i            L'indice de la première arête à échanger.
     * @param j            L'indice de la seconde arête à échanger.
     * @param data         Les données du TSP contenant les distances entre les villes.
     * @return Le gain de longueur obtenu par le 2-échange (valeur négative si la longueur est réduite).
     */
    static long calculateGain(int[] extendedTour, int i, int j, TspData data) {
        int nextI = extendedTour[i + 1];
        int nextJ = extendedTour[j + 1];

        // Calcul des distances des arêtes retirées
        int removedDistance = data.getDistance(extendedTour[i], extendedTour[j]) + data.getDistance(nextI, nextJ);

        // Calcul des distances des nouvelles arêtes ajoutées
        int addedDistance = data.getDistance(extendedTour[i], nextI) + data.getDistance(extendedTour[j], nextJ);

        return removedDistance - addedDistance;
    }

    /**
     * Applique un 2-échange en inversant la sous-séquence d'une tournée.
     *
     * @param extendedTour Le tableau représentant la tournée étendue (circularité incluse).
     * @param i            L'indice de début de la sous-séquence à inverser (inclus).
     * @param j            L'indice de fin de la sous-séquence à inverser (inclus).
     */
    static void applyTwoOptSwap(int[] extendedTour, int i, int j) {
        // Inverser les éléments entre i et j
        while (i < j) {
            int temp = extendedTour[i];
            extendedTour[i] = extendedTour[j];
            extendedTour[j] = temp;
            ++i;
            --j;
        }
    }
}
