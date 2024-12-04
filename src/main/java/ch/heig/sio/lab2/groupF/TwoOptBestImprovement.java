package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspTour;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TwoOptBestImprovement implements ObservableTspImprovementHeuristic {

    @Override
    public TspTour computeTour(TspTour initialTour, TspHeuristicObserver observer) {
        int[] tour = initialTour.tour().copy(); // Copie de la tournée initiale
        int n = tour.length;
        long currentLength = initialTour.length();
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            long bestGain = 0;
            int bestI = -1, bestJ = -1;

            // Parcours des paires (i, j)
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 2; j < n; j++) { // j = i + 1 n'a pas de sens
                    if (j == i + 1) continue;

                    // Calcul du gain
                    long gain = calculateGain(tour, i, j, initialTour);

                    if (gain < bestGain) { // Chercher les réductions de longueur les plus grands
                        bestGain = gain;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            // Si une amélioration est trouvée
            if (bestGain < 0) {
                improvement = true;
                // Appliquer le meilleur 2-échange
                applyTwoOptSwap(tour, bestI + 1, bestJ);
                currentLength += bestGain;

                // Notifier l'observateur
                observer.update(toEdges(tour));
            }
        }

        // Retourner la nouvelle tournée
        return new TspTour(initialTour.data(), tour, currentLength);
    }

    /**
     * Calcule le gain d'un 2-échange (i, j).
     */
    private long calculateGain(int[] tour, int i, int j, TspTour initialTour) {
        int n = tour.length;
        int prevI = tour[i];
        int nextI = tour[(i + 1) % n];
        int prevJ = tour[j];
        int nextJ = tour[(j + 1) % n];

        return initialTour.data().getDistance(prevI, prevJ)
                + initialTour.data().getDistance(nextI, nextJ)
                - initialTour.data().getDistance(prevI, nextI)
                - initialTour.data().getDistance(prevJ, nextJ);
    }

    /**
     * Applique un 2-échange en inversant la sous-séquence de `tour`.
     */
    private void applyTwoOptSwap(int[] tour, int i, int j) {
        while (i < j) {
            int temp = tour[i];
            tour[i] = tour[j];
            tour[j] = temp;
            i++;
            j--;
        }
    }

    /**
     * Convertit la tournée en un tableau d'arêtes pour l'observateur.
     */
    private Iterator<Edge> toEdges(int[] tour) {
        List<Edge> edges = new ArrayList<>();
        int n = tour.length;
        for (int i = 0; i < n; i++) {
            edges.add(new Edge(tour[i], tour[(i + 1) % n]));
        }
        return edges.iterator();
    }
}
