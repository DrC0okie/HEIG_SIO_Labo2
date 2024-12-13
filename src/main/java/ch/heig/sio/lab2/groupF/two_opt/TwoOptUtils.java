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
}
