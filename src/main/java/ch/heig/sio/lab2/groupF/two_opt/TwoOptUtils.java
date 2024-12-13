package ch.heig.sio.lab2.groupF.two_opt;

import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class TwoOptUtils {
    private TwoOptUtils() {
    }

    /**
     * Calcule le gain d'un 2-échange (i, j).
     */
    static long calculateGain(int[] extendedTour, int i, int j, TspData data) {
        int nextI = extendedTour[i + 1];
        int nextJ = extendedTour[j + 1];
        int removedDistance = data.getDistance(extendedTour[i], extendedTour[j]) + data.getDistance(nextI, nextJ);
        int addedDistance = data.getDistance(extendedTour[i], nextI) + data.getDistance(extendedTour[j], nextJ);
        return removedDistance - addedDistance;
    }

    /**
     * Applique un 2-échange en inversant la sous-séquence de `tour`.
     */
    static void applyTwoOptSwap(int[] extendedTour, int i, int j) {
        while (i < j) {
            int temp = extendedTour[i];
            extendedTour[i] = extendedTour[j];
            extendedTour[j] = temp;
            ++i;
            --j;
        }
    }

    /**
     * Convertit la tournée en un tableau d'arêtes pour l'observateur.
     */
    static Iterator<Edge> toEdges(int[] extendedTour) {
        List<Edge> edges = new ArrayList<>();
        int n = extendedTour.length - 1; // Exclure l'élément ajouté
        for (int i = 0; i < n; i++) {
            edges.add(new Edge(extendedTour[i], extendedTour[i + 1]));
        }
        return edges.iterator();
    }
}
