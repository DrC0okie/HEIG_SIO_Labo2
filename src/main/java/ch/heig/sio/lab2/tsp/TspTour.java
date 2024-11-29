package ch.heig.sio.lab2.tsp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Record storing a solution for an instance of the TSP.
 *
 * @param data   Reference to problem instance
 * @param tour   An unmodifiable {@link View} of the array storing the permutation of cities in the tour
 * @param length Length of the tour
 */
public record TspTour(TspData data, View tour, long length) {
	/**
	 * An unmodifiable view of the array storing the permutation of cities in the tour.
	 */
	public static final class View implements Iterable<Integer> {
		private final int[] tour;

		/**
		 * @param tour Array storing the permutation of cities in the tour
		 */
		public View(int[] tour) {
			this.tour = tour;
		}

		/**
		 * @param i Index to access
		 * @return City at position i in the tour
		 *
		 * @throws ArrayIndexOutOfBoundsException if {@code i < 0} or {@code i >= size()}
		 */
		public int get(int i) {
			return tour[i];
		}

		/**
		 * @return Number of cities in the tour
		 */
		public int size() {
			return tour.length;
		}

		/**
		 * @return A copy of the array storing the permutation of cities in the tour
		 */
		public int[] copy() {
			return Arrays.copyOf(tour, tour.length);
		}

		/**
		 * Copy a range of cities in the tour, padding with 0 all indexes that do not exist in the tour.
		 *
		 * @param from Start index (inclusive)
		 * @param to End index (exclusive)
		 * @return A copy of the range of cities in the tour of size {@code to - from}
		 *
		 * @throws ArrayIndexOutOfBoundsException if {@code from < 0} or {@code from > size()}
		 * @throws IllegalArgumentException if {@code from > to}
		 */
		public int[] copyRange(int from, int to) {
			return Arrays.copyOfRange(tour, from, to);
		}

		/**
		 * @return Stream of cities in the tour
		 */
		public IntStream stream() {
			return Arrays.stream(tour);
		}

		@Override
		public Iterator<Integer> iterator() {
			return stream().iterator();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof View integers))
				return false;
      return Arrays.equals(tour, integers.tour);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(tour);
		}
	}

	/**
	 * @param data   Reference to problem instance
	 * @param tour   Array storing the permutation of cities in the tour. Ownership should be transferred to the record.
	 * @param length Length of the tour
	 */
	public TspTour(TspData data, int[] tour, long length) {
		this(data, new View(tour), length);
	}

	/**
	 * @return String representation of the current tour
	 */
	public String toString() {
		return "Length: " + length + ", "
				+ "Tour: " + Arrays.toString(tour.tour);
	}
}