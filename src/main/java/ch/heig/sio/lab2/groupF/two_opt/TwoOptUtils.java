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
    static long calculateGain(int[] tour, int i, int j, TspData data) {
        int nextI = tour[i + 1];
        int nextJ = tour[(j + 1) % tour.length];
        int removedDistance = data.getDistance(tour[i], tour[j]) + data.getDistance(nextI, nextJ);
        int addedDistance = data.getDistance(tour[i], nextI) + data.getDistance(tour[j], nextJ);
        return removedDistance - addedDistance;
    }

    /**
     * Applique un 2-échange en inversant la sous-séquence de `tour`.
     */
    static void applyTwoOptSwap(int[] tour, int i, int j) {
        while (i < j) {
            int temp = tour[i];
            tour[i] = tour[j];
            tour[j] = temp;
            ++i;
            --j;
        }
    }

    /**
     * Convertit la tournée en un tableau d'arêtes pour l'observateur.
     */
    static Iterator<Edge> toEdges(int[] tour) {
        List<Edge> edges = new ArrayList<>();
        int n = tour.length;
        for (int i = 0; i < n; i++) {
            edges.add(new Edge(tour[i], tour[(i + 1) % n]));
        }
        return edges.iterator();
    }
}
