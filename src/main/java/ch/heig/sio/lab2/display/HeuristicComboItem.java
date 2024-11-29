package ch.heig.sio.lab2.display;

import ch.heig.sio.lab2.tsp.TspConstructiveHeuristic;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

/**
 * <p>Thin wrapper around an heuristic to display its name in combo box.</p>
 *
 * <p>Use {@link HeuristicComboItem.Constructive} or {@link HeuristicComboItem.Improvement} to create instances.</p>
 *
 * @param <T> Type of the heuristic, either {@link ObservableTspConstructiveHeuristic} or
 *            {@link ObservableTspImprovementHeuristic}
 */
public abstract sealed class HeuristicComboItem<T> {

  /**
   * Wrapper around {@link ObservableTspConstructiveHeuristic} to display heuristic name in combo box.
   */
  public static final class Constructive extends HeuristicComboItem<ObservableTspConstructiveHeuristic> implements
      ObservableTspConstructiveHeuristic {

    /**
     * Creates a combo item for a constructive heuristic that is observable.
     * @param name Name of the heuristic
     * @param heuristic Heuristic to wrap
     */
    public Constructive(String name, ObservableTspConstructiveHeuristic heuristic) {
      super(name, heuristic);
    }

    /**
     * Creates a combo item for a constructive heuristic that is not observable. Will be wrapped in an observable
     * heuristic that never notifies observers.
     *
     * @param name      Name of the heuristic
     * @param heuristic Heuristic to wrap
     */
    public Constructive(String name, TspConstructiveHeuristic heuristic) {
      this(name, (data, startCityIndex, observer) -> heuristic.computeTour(data, startCityIndex));
    }

    @Override
    public TspTour computeTour(TspData data, int startCityIndex, TspHeuristicObserver observer) {
      return super.heuristic.computeTour(data, startCityIndex, observer);
    }
  }

  /**
   * Wrapper around {@link ObservableTspImprovementHeuristic} to display heuristic name in combo box.
   */
  public static final class Improvement extends HeuristicComboItem<ObservableTspImprovementHeuristic> implements
      ObservableTspImprovementHeuristic {

    /**
     * Creates a combo item for an improvement heuristic that is observable.
     * @param name Name of the heuristic
     * @param heuristic Heuristic to wrap
     */
    public Improvement(String name, ObservableTspImprovementHeuristic heuristic) {
      super(name, heuristic);
    }

    @Override
    public TspTour computeTour(TspTour initialTour, TspHeuristicObserver observer) {
      return super.heuristic.computeTour(initialTour, observer);
    }
  }

  /** Name of the heuristic. */
  private final String name;
  /** Heuristic to wrap. */
  private final T heuristic;

  private HeuristicComboItem(String name, T heuristic) {
    this.name = name;
    this.heuristic = heuristic;
  }

  @Override
  public String toString() {
    return name;
  }
}
