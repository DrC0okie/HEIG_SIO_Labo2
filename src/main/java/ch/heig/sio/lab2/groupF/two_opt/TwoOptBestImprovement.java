package ch.heig.sio.lab2.groupF.two_opt;

import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static ch.heig.sio.lab2.groupF.two_opt.TwoOptUtils.*;

public class TwoOptBestImprovement implements ObservableTspImprovementHeuristic {

    @Override
    public TspTour computeTour(TspTour initialTour, TspHeuristicObserver observer) {
        int[] tour = initialTour.tour().copy(); // Copie de la tournée initiale
        final TspData data = initialTour.data();

        // Étendre le tableau pour gérer la circularité
        int[] extendedTour = new int[tour.length + 1];
        System.arraycopy(tour, 0, extendedTour, 0, tour.length);
        extendedTour[tour.length] = tour[0]; // Ajouter la première ville à la fin

        int n = tour.length;
        long currentLength = initialTour.length();
        boolean hasImproved = true;

        while (hasImproved) {
            hasImproved = false;
            long bestGain = 0;
            int bestI = -1, bestJ = -1;

            // Parcours des paires (i, j)
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 2; j < n; j++) {
                    // Calcul du gain
                    long gain = calculateGain(extendedTour, i, j, data);

                    if (gain < bestGain) { // Chercher les réductions de longueur les plus grandes
                        bestGain = gain;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
            // Si une amélioration est trouvée
            if (bestGain < 0) {
                hasImproved = true;
                // Appliquer le meilleur 2-échange
                applyTwoOptSwap(extendedTour, bestI + 1, bestJ);
                currentLength += bestGain;

                // Notifier l'observateur
                observer.update(new EdgeIterator(extendedTour));
            }
        }

        // Créer le tableau final sans l'élément supplémentaire
        int[] finalTour = new int[tour.length];
        System.arraycopy(extendedTour, 0, finalTour, 0, tour.length);
        return new TspTour(data, finalTour, currentLength);
    }

    /**
     * A private static inner class to lazily iterate over edges of the extended tour.
     */
    private static class EdgeIterator implements Iterator<Edge> {
        private final int[] extendedTour;
        private int currentIndex;
        private final int edgeCount;

        /**
         * Constructs an EdgeIterator for a given tour.
         *
         * @param extendedTour The extended tour array (circular).
         */
        public EdgeIterator(int[] extendedTour) {
            this.extendedTour = extendedTour;
            this.currentIndex = 0;
            this.edgeCount = extendedTour.length - 1; // Exclude the duplicate closing city
        }

        @Override
        public boolean hasNext() {
            return currentIndex < edgeCount;
        }

        @Override
        public Edge next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return new Edge(extendedTour[currentIndex], extendedTour[++currentIndex]);
        }
    }
}
