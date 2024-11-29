package ch.heig.sio.lab2.tsp;

import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * <p>Builds a random tour for the TSP.</p>
 *
 * <p>All tours computed with a given instance use the same pseudorandom number generator instance. Thus, if a seed
 * is set, two consecutive calls to {@link RandomTour#computeTour} won't give the same tour, but two
 * instances initialized with the same seed will give the same tours (for the same number of cities, obviously).</p>
 *
 * <p>Space and time complexity is linear with the number of cities.</p>
 */
public final class RandomTour implements TspConstructiveHeuristic {
  private final RandomGenerator rnd;

  /**
   * Builds a new instance with no seed.
   */
  public RandomTour() {
    this(new Random());
  }

  /**
   * Builds a new instance with the given seed for the pseudorandom generator.
   * @param seed A seed
   */
  public RandomTour(long seed) {
    this(new Random(seed));
  }

  /**
   * Builds a new instance with the given pseudorandom generator.
   * @param rnd A pseudorandom generator
   */
  public RandomTour(RandomGenerator rnd) {
    this.rnd = rnd;
  }

  @Override
  public TspTour computeTour(TspData data, int startCity) {
    // A tour as a permutation of the cities
    int[] tour = new int[data.getNumberOfCities()];

    // Initialization: put cities in order
    for (int i = 0; i < tour.length; ++i) {
      tour[i] = i;
    }

    int length = 0;

    // Shuffle
    for (int i = tour.length - 1; i > 0; --i) {
      int j = rnd.nextInt(i + 1);

      // Swap
      int tmp = tour[i];
      tour[i] = tour[j];
      tour[j] = tmp;

      // We need at least two cities to compute a distance
      if (i < tour.length - 1) {
        // Add the distance between the new city and the previous one
        length += data.getDistance(tour[i], tour[i + 1]);
      }
    }

    //
    length += data.getDistance(tour[0], tour[1]);
    // Add the distance between the last and the first city
    length += data.getDistance(tour[0], tour[tour.length - 1]);

    return new TspTour(data, tour, length);
  }
}
