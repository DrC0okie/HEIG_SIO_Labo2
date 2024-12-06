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

        // Initialisation de la matrice des gains
        long[][] gainMatrix = new long[n][n];

        // Pré-calcul des gains pour toutes les paires (i, j) en O(n^2)
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 2; j < n; j++) { // j = i + 1 n'a pas de sens
                gainMatrix[i][j] = calculateGain(tour, i, j, initialTour);
            }
        }

        while (improvement) {
            improvement = false;
            long bestGain = initialTour.length(); // Chercher le gain le plus petit (négatif)
            int bestI = -1, bestJ = -1;

            // Rechercher le meilleur gain dans la matrice en O(n^2)
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 2; j < n; j++) {
                    if (gainMatrix[i][j] < bestGain) {
                        bestGain = gainMatrix[i][j];
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            // Si une amélioration est trouvée
            if (bestGain < 0) { // Gain négatif -> amélioration
                improvement = true;
                applyTwoOptSwap(tour, bestI + 1, bestJ);
                currentLength += bestGain; // Ajouter le gain (qui est négatif)

                // Mettre à jour uniquement les paires affectées dans la matrice en O(k) ou k est proportionnel à n
                updateGainMatrix(gainMatrix, tour, bestI, bestJ, initialTour);
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
        int nextI = tour[i + 1];
        int nextJ = tour[(j + 1) % tour.length];

        return initialTour.data().getDistance(tour[i], tour[j])
                + initialTour.data().getDistance(nextI, nextJ)
                - initialTour.data().getDistance(tour[i], nextI)
                - initialTour.data().getDistance(tour[j], nextJ);
    }

    /**
     * Applique un 2-échange à une tournée en inversant une sous-séquence du tableau représentant la tournée.
     *
     * @param tour Le tableau représentant la tournée. Chaque élément correspond à une ville.
     * @param i    L'indice de début (inclus) de la sous-séquence à inverser.
     * @param j    L'indice de fin (inclus) de la sous-séquence à inverser.
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

    private void updateGainMatrix(long[][] gainMatrix, int[] tour, int bestI, int bestJ, TspTour initialTour) {
        int n = tour.length;

        // Mettre à jour les paires affectées par l'échange (zones autour de bestI et bestJ)
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 2; j < n; j++) {
                if (Math.abs(i - bestI) <= 2 || Math.abs(j - bestJ) <= 2) {
                    gainMatrix[i][j] = calculateGain(tour, i, j, initialTour);
                }
            }
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
