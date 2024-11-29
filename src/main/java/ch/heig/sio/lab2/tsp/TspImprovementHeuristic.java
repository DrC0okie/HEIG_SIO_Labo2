package ch.heig.sio.lab2.tsp;

/**
 * <p>An improvement heuristic for the TSP, that will compute a tour for a given instance.</p>
 *
 * <p>Improvement heuristics are used to refine an existing tour, and are typically used after a constructive
 * heuristic.</p>
 */
@FunctionalInterface
public interface TspImprovementHeuristic {
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
   * @throws NullPointerException if {@code initialTour} is null
   */
  TspTour computeTour(TspTour initialTour);
}