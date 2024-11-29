package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

import static java.lang.Math.max;

public class TwoOptBestImprovement implements ObservableTspImprovementHeuristic{


    private TwoSwap searchBestTwoOpt (TspTour tspTour){
        TspTour.View tour = tspTour.tour();
        TwoSwap bestSwap = new TwoSwap(new Edge(-1,-1), new Edge(-1,-1));
        long improvement = 0;
        for (int i = 0; i < tour.size(); i++ ){
            int currentCity = tour.get(i);
            for(int j = i + 2; j < tour.size(); j++){
                improvement = max(improvement, calculateSwap(tspTour.data(), i, j));

                if(calculateSwap(tspTour.data(), i, j) > improvement){
                    bestSwap.setFirst();
                }
            }
        }
    }

    private long calculateSwap(TspData tour, int i, int j) {
        return tour.getDistance(i, i+1) + tour.getDistance(j, j+1) -
                tour.getDistance(i, j) - tour.getDistance(i+1, j+1);
    }


    public TspTour computeTour(TspTour initialTour, TspHeuristicObserver observer) {
//        Rechercher le meilleur 2-échange pour la tournée actuelle




//        Effectuer le 2-échange s’il est améliorant


        return null;
    }
}
