package ch.heig.sio.lab2.groupF.statistics;

import ch.heig.sio.lab2.tsp.TspTour;
import java.util.*;

public class Statistics {
    private final String heuristicName;
    private final long optimalLength;

    private final List<Long> initialLengths = new ArrayList<>();
    private final List<Long> improvedLengths = new ArrayList<>();
    private final List<Long> times = new ArrayList<>();

    private SummaryStatistics initialStats;
    private SummaryStatistics improvedStats;

    private double avgTimeMs;

    public Statistics(String heuristicName, long optimalLength) {
        this.heuristicName = heuristicName;
        this.optimalLength = optimalLength;
    }

    public void addInitialLength(long length) {
        initialLengths.add(length);
    }

    public void addImprovedLength(long length) {
        improvedLengths.add(length);
    }

    public void addTime(long timeNano) {
        times.add(timeNano);
    }

    public void computeStatistics() {
        initialStats = SummaryStatistics.calculate(initialLengths, optimalLength);
        improvedStats = SummaryStatistics.calculate(improvedLengths, optimalLength);
        avgTimeMs = times.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1_000_000.0;
    }

    public void print() {

        System.out.printf("%-18s | %-9s | %s%n", heuristicName, "Initial", initialStats);
        System.out.printf("%-18s | %-9s | %s%n", "", "Optimized", improvedStats);
        System.out.printf("2-opt avg compute time: %.0f ms%n%n", avgTimeMs);
    }

    public static void printStatisticsTable(List<Statistics> allStats) {
        System.out.printf("%-18s | %-9s | %17s | %17s | %17s | %6s%n",
                "Heuristic", "Status", "Min", "Avg", "Max", "Median");
        System.out.println("-".repeat(99));

        allStats.forEach(stat -> {
            stat.computeStatistics();
            stat.print();
        });
    }

    public static int[] getFirstNCities(TspTour tour, int n) {
        return tour.tour().stream().limit(n).toArray();
    }
}
