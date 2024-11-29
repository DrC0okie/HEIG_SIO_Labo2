package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.display.HeuristicComboItem;
import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.display.ObservableTspImprovementHeuristic;
import ch.heig.sio.lab2.display.TspSolverGui;
import ch.heig.sio.lab2.tsp.RandomTour;
import com.formdev.flatlaf.FlatLightLaf;

public final class Gui {
  public static void main(String[] args) {
    ObservableTspConstructiveHeuristic[] constructiveHeuristics = {
        new HeuristicComboItem.Constructive("Random tour", new RandomTour()),
        new HeuristicComboItem.Constructive("Random Insert",new RandomInsert()),
        new HeuristicComboItem.Constructive("Nearest Insert", new NearestInsert()),
        new HeuristicComboItem.Constructive("Furthest Insert", new FurthestInsert()),

    };

    ObservableTspImprovementHeuristic[] improvementHeuristics = {
        // Add the new improvement heuristic
    };

    // May not work on all platforms, comment out if necessary
    System.setProperty("sun.java2d.opengl", "true");
    FlatLightLaf.setup();

    new TspSolverGui(1400, 800, "TSP solver", constructiveHeuristics, improvementHeuristics);
  }
}
