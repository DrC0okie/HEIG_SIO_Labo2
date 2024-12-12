package ch.heig.sio.lab2.groupF.statistics;

import java.util.Collections;
import java.util.List;

public class SummaryStatistics {
    private final long min;
    private final double avg;
    private final long max;
    private final double median;
    private final double relativeErrorMin;
    private final double relativeErrorAvg;
    private final double relativeErrorMax;

    private SummaryStatistics(long min, double avg, long max, double median,
                              double relativeErrorMin, double relativeErrorAvg, double relativeErrorMax) {
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.median = median;
        this.relativeErrorMin = relativeErrorMin;
        this.relativeErrorAvg = relativeErrorAvg;
        this.relativeErrorMax = relativeErrorMax;
    }

    public static SummaryStatistics calculate(List<Long> values, long optimalLength) {
        if (values.isEmpty()) {
            return new SummaryStatistics(0, 0, 0, 0, 0, 0, 0);
        }

        Collections.sort(values);

        long min = values.getFirst();
        long max = values.getLast();
        double avg = values.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
        double median = values.size() % 2 == 0
                ? (values.get(values.size() / 2 - 1) + values.get(values.size() / 2)) / 2.0
                : values.get(values.size() / 2);

        double relativeErrorMin = 100.0 * (min - optimalLength) / optimalLength;
        double relativeErrorAvg = 100.0 * (avg - optimalLength) / optimalLength;
        double relativeErrorMax = 100.0 * (max - optimalLength) / optimalLength;

        return new SummaryStatistics(min, avg, max, median,
                relativeErrorMin, relativeErrorAvg, relativeErrorMax);
    }

    @Override
    public String toString() {
        return String.format("%6d %10s | %6.0f %10s | %6d %10s | %6.0f",
                min, formatPercent(relativeErrorMin),
                avg, formatPercent(relativeErrorAvg),
                max, formatPercent(relativeErrorMax),
                median);
    }

    private String formatPercent(double percent) {
        // Format the percent string with a fixed width, including parentheses
        return String.format("(%.2f%%)", percent);
    }

}
