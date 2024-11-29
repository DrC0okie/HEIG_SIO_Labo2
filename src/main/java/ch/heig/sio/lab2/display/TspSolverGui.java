package ch.heig.sio.lab2.display;

import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspParsingException;
import ch.heig.sio.lab2.tsp.TspTour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * GUI class for the TSPSolver.
 *
 * @author Noah Boegli
 */
public final class TspSolverGui {
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
  private static final Random RND = new Random();
  private static final ObservableTspImprovementHeuristic NO_IMPROVEMENT
      = new HeuristicComboItem.Improvement("No improvement", null);

  private Animation animation;

  private TspData currentData;
  private int delay;
  private String filePath;

  // Swing components
  private final JFrame window;
  private final JPanel optionsPanel;
  private final JLabel currentFileLabel;
  private final JLabel resultsLabel;
  private final FileDialog dataFileDialog;
  private final JButton runContinuouslyButton;
  private final JButton stopRunButton;
  private final JButton runOneStepButton;
  private final JButton resetButton;
  private final JButton showFilePickerButton;
  private final JComboBox<ObservableTspImprovementHeuristic> improvementHeuristicChoice;
  private final JComboBox<ObservableTspConstructiveHeuristic> constructiveHeuristicChoice;
  private final JComboBox<Integer> startCityIndexChoice;
  private final JComboBox<String> speedChoice;
  private final JCheckBox showCityLabels;
  private final TspDisplayArea displayArea;

  // Configuration for the topbar height
  private static final int TOPBAR_HEIGHT = 50;

  /**
   * Private constructor, used by `getInstance()`.
   *
   * @param width  The window width.
   * @param height The window height.
   * @param title  The window title.
   */
  public TspSolverGui(int width,
                      int height,
                      String title,
                      ObservableTspConstructiveHeuristic[] constructiveHeuristics,
                      ObservableTspImprovementHeuristic[] improvementHeuristics) {
    ////////////////////////////////////////////////////////////
    // Class setup
    ////////////////////////////////////////////////////////////
    this.delay = 100;

    ////////////////////////////////////////////////////////////
    // Base window
    ////////////////////////////////////////////////////////////
    this.window = new JFrame();
    this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.window.setSize(width, height);
    this.window.setTitle(title);
    window.setLayout(new BorderLayout());
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        if (animation != null)
          animation.cancel();
      }
    });

    ////////////////////////////////////////////////////////////
    // Main canvas
    ////////////////////////////////////////////////////////////
    this.displayArea = new TspDisplayArea();
    this.displayArea.setSize(width, height - TspSolverGui.TOPBAR_HEIGHT);


    ////////////////////////////////////////////////////////////
    // Options bar (topbar) component
    ////////////////////////////////////////////////////////////
    this.optionsPanel = new JPanel();
    this.optionsPanel.setPreferredSize(new Dimension(width, TspSolverGui.TOPBAR_HEIGHT));
    this.optionsPanel.setBounds(0, 0, width, TspSolverGui.TOPBAR_HEIGHT);


    ////////////////////////////////////////////////////////////
    // Configuration components
    ////////////////////////////////////////////////////////////
    // Heuristic choice
    improvementHeuristics = Arrays.copyOf(improvementHeuristics, improvementHeuristics.length + 1);
    improvementHeuristics[improvementHeuristics.length - 1] = NO_IMPROVEMENT;
    this.improvementHeuristicChoice = new JComboBox<>(improvementHeuristics);
    improvementHeuristicChoice.setEnabled(false);
    this.constructiveHeuristicChoice = new JComboBox<>(constructiveHeuristics);
    constructiveHeuristicChoice.setEnabled(false);

    // Speed choice
    String[] speeds = {"Instantaneous", "Ludicrous", "Slow", "Slower", "Snail"};
    this.speedChoice = new JComboBox<>(speeds);
    speedChoice.setSelectedIndex(2);
    speedChoice.setEnabled(false);
    speedChoice.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        String selectedSpeed = (String) e.getItem();
        delay = switch (selectedSpeed) {
          case "Instantaneous" -> 0;
          case "Ludicrous" -> 5;
          case "Slower" -> 300;
          case "Snail" -> 800;
          default -> 100;
        };

        if (animation != null)
          animation.setDelay(delay);
      }
    });

    // Labels toggle
    this.showCityLabels = new JCheckBox("Show labels");
    showCityLabels.setSelected(false);
    showCityLabels.addItemListener(e -> {
      displayArea.setShowLabels(showCityLabels.isSelected());
    });

    // Start city index choice
    this.startCityIndexChoice = new JComboBox<>();
    this.startCityIndexChoice.setEnabled(false);

    // Run continuously button
    this.runContinuouslyButton = new JButton("Run continuously");
    runContinuouslyButton.setEnabled(false);
    runContinuouslyButton.addActionListener(e -> {
      this.runContinuously();
    });

    // Stop button
    this.stopRunButton = new JButton("Stop");
    stopRunButton.setEnabled(false);
    stopRunButton.addActionListener(e -> {
      this.stop();
    });

    // Run one step button
    this.runOneStepButton = new JButton("Run one step");
    runOneStepButton.setEnabled(false);
    runOneStepButton.addActionListener(e -> {
      this.initHeuristic();
      animation.doStep();
    });

    // Reset button
    this.resetButton = new JButton("Reset");
    resetButton.setEnabled(false);
    resetButton.addActionListener(e -> {
      this.reset(false);
    });

    // Results label
    this.resultsLabel = new JLabel("Results: Awaiting execution");

    // File picker & associated button (with label)
    this.showFilePickerButton = new JButton("Select file to run");
    this.currentFileLabel = new JLabel("Current file: None");
    this.dataFileDialog = new FileDialog(this.window);
    dataFileDialog.setFilenameFilter((dir, name) -> name.endsWith(".dat"));
    showFilePickerButton.addActionListener(e -> {
      dataFileDialog.setDirectory(Path.of("").toAbsolutePath().toString());
      dataFileDialog.setVisible(true);

      var dir = dataFileDialog.getDirectory();
      var file = dataFileDialog.getFile();
      if (dir == null || file == null) {
        return;
      }

      this.filePath = Path.of(dir, file).toString();
      init();
    });


    ////////////////////////////////////////////////////////////
    // Configuring the options bar & adding all the components
    ////////////////////////////////////////////////////////////
    this.optionsPanel.setLayout(new FlowLayout());

    // File picker & current file
    this.optionsPanel.add(showFilePickerButton);
    this.optionsPanel.add(this.currentFileLabel);
    this.optionsPanel.add(this.createVerticalSeparator());

    // Options
    this.optionsPanel.add(this.improvementHeuristicChoice);
    this.optionsPanel.add(this.constructiveHeuristicChoice);
    this.optionsPanel.add(this.startCityIndexChoice);
    this.optionsPanel.add(this.showCityLabels);
    this.optionsPanel.add(this.createVerticalSeparator());

    // Run & results
    this.optionsPanel.add(this.speedChoice);
    this.optionsPanel.add(this.runContinuouslyButton);
    this.optionsPanel.add(this.stopRunButton);
    this.optionsPanel.add(this.runOneStepButton);
    this.optionsPanel.add(this.resetButton);
    this.optionsPanel.add(this.createVerticalSeparator());
    this.optionsPanel.add(this.resultsLabel);

    ////////////////////////////////////////////////////////////
    // Showing everything
    ////////////////////////////////////////////////////////////
    this.window.add(this.optionsPanel, BorderLayout.NORTH);
    this.window.add(displayArea, BorderLayout.CENTER);
    this.displayArea.setVisible(true);
    this.optionsPanel.setVisible(true);
    this.window.setVisible(true);
  }

  /**
   * Creates a vertical separator.
   *
   * @return A vertical JSeparator.
   */
  private JComponent createVerticalSeparator() {
    JSeparator x = new JSeparator(SwingConstants.VERTICAL);
    x.setPreferredSize(new Dimension(3, TspSolverGui.TOPBAR_HEIGHT));
    return x;
  }

  /**
   * Inits the animation with the chosen heuristic.
   */
  private void initAnimation() {
    // Instantiating the correct heuristic
    Object chosenConstructiveHeuristicItem = this.constructiveHeuristicChoice.getSelectedItem();
    if (chosenConstructiveHeuristicItem == null) {
      JOptionPane.showMessageDialog(
          this.window,
          "An unknown error happened",
          "Error",
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    var chosenConstructiveHeuristic = (ObservableTspConstructiveHeuristic) chosenConstructiveHeuristicItem;
    var chosenImprovementHeuristic = (ObservableTspImprovementHeuristic) this.improvementHeuristicChoice.getSelectedItem();

    Object startCityChosenItem = this.startCityIndexChoice.getSelectedItem();
    if (startCityChosenItem == null) {
      JOptionPane.showMessageDialog(
          this.window,
          "An unknown error happened",
          "Error",
          JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    int startCity = (Integer) startCityChosenItem == -1
        ? RND.nextInt(currentData.getNumberOfCities())
        : (Integer) startCityChosenItem;

    // Disabling heuristic choice related components
    this.improvementHeuristicChoice.setEnabled(false);
    this.constructiveHeuristicChoice.setEnabled(false);
    this.startCityIndexChoice.setEnabled(false);

    // Enabling the reset button
    this.resetButton.setEnabled(true);

    animation = new Animation(displayArea, delay);

    Supplier<TspTour> callable;
    if (chosenImprovementHeuristic == NO_IMPROVEMENT) {
      callable = () -> chosenConstructiveHeuristic.computeTour(currentData, startCity, animation);
    } else {
      TspTour initialTour = chosenConstructiveHeuristic.computeTour(currentData, startCity);
      displayArea.update(toIterator(initialTour));
      callable = () -> chosenImprovementHeuristic.computeTour(initialTour, animation);
    }

    CompletableFuture.supplyAsync(callable, EXECUTOR_SERVICE)
        .whenComplete((tour, throwable) -> {
          if (throwable != null && !(throwable.getCause() instanceof CancelledAnimationException)) {
            throwable.printStackTrace();
            JOptionPane.showMessageDialog(
                this.window,
                "An error happened while building the tour.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            reset(false);
          }

          heuristicComplete(tour);
        });
  }

  /**
   * Converts a TspTour to an iterator of edges.
   *
   * @param tspTour The TspTour to convert.
   * @return An iterator of edges.
   */
  private static Iterator<Edge> toIterator(TspTour tspTour) {
    TspTour.View tour = tspTour.tour();
    return IntStream.range(0, tour.size())
            .mapToObj(i -> new Edge(tour.get(i), tour.get((i + 1) % tour.size())))
            .iterator();
  }

  /**
   * Does the clean-up & post-heuristic actions.
   */
  private void heuristicComplete(TspTour tspTour) {
    displayArea.update(toIterator(tspTour));
    this.resultsLabel.setText("Done. Distance is " + tspTour.length());
    this.reset(true);
  }

  /**
   * Does some initialisation work when starting the heuristic.
   */
  private void initHeuristic() {
    if (this.animation == null)
      this.initAnimation();

    // Updating the status label
    this.resultsLabel.setText("Distance: Computing...");
  }

  /**
   * Starts (or restarts) a continuous run.
   */
  private void runContinuously() {
    this.initHeuristic();

    // Disabling the 2 runs buttons
    this.runContinuouslyButton.setEnabled(false);
    this.runOneStepButton.setEnabled(false);

    // Enabling the stop button
    this.stopRunButton.setEnabled(true);

    animation.resume();
  }

  /**
   * Stops a continuous execution.
   */
  private void stop() {
    animation.pause();

    // Enabling the 2 runs buttons
    this.runContinuouslyButton.setEnabled(true);
    this.runOneStepButton.setEnabled(true);

    // Disabling the run button
    this.stopRunButton.setEnabled(false);
  }

  /**
   * Resets the state.
   *
   * @param heuristicComplete Whether it's a reset in the middle of a heuristic or at the end of one.
   */
  private void reset(boolean heuristicComplete) {
    if (animation != null) {
      animation.cancel();
      animation = null;
    }

    // Enabling the heuristic choice component
    this.improvementHeuristicChoice.setEnabled(true);
    this.constructiveHeuristicChoice.setEnabled(true);

    // Enabling the speed & start city components
    this.startCityIndexChoice.setEnabled(true);
    this.speedChoice.setEnabled(true);

    // Enabling the run buttons
    this.runContinuouslyButton.setEnabled(true);
    this.runOneStepButton.setEnabled(true);

    // Disabling the reset button
    this.resetButton.setEnabled(false);

    // Disabling the stop button
    this.stopRunButton.setEnabled(false);

    // Updating results label
    if (!heuristicComplete) {
      this.resultsLabel.setText("Results: Awaiting execution");
      displayArea.reset(currentData);
    }
  }

  /**
   * Initialises the data by loading the file.
   */
  private void init() {
    try {
      // TSP Solving data
      currentData = TspData.fromFile(this.filePath);
      this.currentFileLabel.setText("Current file: " + this.dataFileDialog.getFile());
      this.startCityIndexChoice.removeAllItems();
      this.startCityIndexChoice.addItem(-1);
      for (int i = 0; i < currentData.getNumberOfCities(); ++i) {
        this.startCityIndexChoice.addItem(i);
      }
      this.startCityIndexChoice.setSelectedIndex(0);

      reset(false);
    } catch (TspParsingException | FileNotFoundException e) {
      JOptionPane.showMessageDialog(
          this.window,
          "Error while parsing the TSP Data",
          "File error",
          JOptionPane.ERROR_MESSAGE
      );
    }
  }
}
