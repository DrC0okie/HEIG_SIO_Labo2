package ch.heig.sio.lab2.display;

import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspImprovementHeuristic;
import ch.heig.sio.lab2.tsp.TspTour;

/**
 * <p>Observable version of {@link TspConstructiveHeuristic}, for use in {@link TspSolverGui}.</p>
 */
public interface ObservableTspImprovementHeuristic extends TspImprovementHeuristic {
  @Override
  default TspTour computeTour(TspTour initialTour) {
    return computeTour(initialTour, v -> {});
  }

  /**
   * <p>Computes a tour for the symmetric travelling salesman problem by improving the given initial tour.</p>
   *
   * <p>Implementations must guarantee that the returned tour isn't worst than the initial tour.</p>
   *
   * <p>If the initial tour is ill-formed, the behavior is undefined.</p>
   *
   * @param initialTour An existing tour to improve
   * @return Solution found by the heuristic
   *
   * @throws NullPointerException if {@code initialTour} or {@code observer} is null
   */
  TspTour computeTour(TspTour initialTour, TspHeuristicObserver observer);
}
