/*
 *  Copyright 2021 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.controller;

import edu.cnm.deepdive.model.Terrain;
import edu.cnm.deepdive.view.TerrainView;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

/**
 * Handles all user interaction to configure, start, stop, and reset Conway's Game of Life, running
 * in a JavaFX scene. The methods and fields are autowired (using the {@link FXML @FXML} annotation)
 * to the associated FXML layout file; in addition, the {@link #stop()} method is exposed, to allow
 * the background threads (used for performing iterations on the CA and scheduling display updates)
 * to be shut down from an override of the JavaFX {@link Application#stop()} method.
 */
public class MainController {

  private static final int DEFAULT_WORLD_SIZE = 500;
  private static final String STOP_KEY = "stop";
  private static final String GENERATION_DISPLAY_KEY = "generationDisplay";
  private static final String POPULATION_DISPLAY_KEY = "populationDisplay";
  private static final String START_KEY = "start";

  private Terrain terrain;
  private Random rng;
  private boolean running;
  private Updater updater;
  private String generationDisplayFormat;
  private String populationDisplayFormat;

  @FXML
  private Text generationDisplay;
  @FXML
  private Text populationDisplay;
  @FXML
  private TerrainView terrainView;
  @FXML
  private ToggleButton toggleRun;
  @FXML
  private Slider densitySlider;
  @FXML
  private Button reset;
  @FXML
  private ResourceBundle resources;

  @FXML
  private void initialize() {
    rng = new Random();
    updater = new Updater();
    generationDisplayFormat = resources.getString(GENERATION_DISPLAY_KEY);
    populationDisplayFormat = resources.getString(POPULATION_DISPLAY_KEY);
    reset(null);
  }

  @FXML
  private void toggleRun(ActionEvent actionEvent) {
    if (toggleRun.isSelected()) {
      start();
    } else {
      stop();
    }
  }

  @FXML
  private void reset(ActionEvent actionEvent) {
    terrain = new Terrain(DEFAULT_WORLD_SIZE, densitySlider.getValue() / 100, rng);
    terrainView.setTerrain(terrain);
    Platform.runLater(this::updateDisplay);
  }

  private void start() {
    running = true;
    toggleRun.setText(resources.getString(STOP_KEY));
    reset.setDisable(true);
    updater.start();
    new Runner().start();
  }

  /**
   * Shuts down animation timer and clears flag for background CA iteration, indirectly terminating
   * processing of any further iterations. This method is called indirectly by UI event
   * listeners, but may also be called from an override of the JavaFX {@link Application#stop()}
   * lifecycle method.
   */
  public void stop() {
    running = false;
    updater.stop();
  }

  private void updateDisplay() {
    long generation = terrain.getIterationCount();
    int population = terrain.getPopulation();
    terrainView.draw();
    generationDisplay.setText(String.format(generationDisplayFormat, generation));
    populationDisplay.setText(String.format(populationDisplayFormat, population));
  }

  private void updateControls() {
    toggleRun.setText(resources.getString(START_KEY));
    toggleRun.setSelected(false);
    reset.setDisable(false);
  }

  private class Runner extends Thread {

    private static final int HISTORY_LENGTH = 24;

    private final List<Long> historyList = new LinkedList<>();
    private final Set<Long> historySet = new HashSet<>();

    @Override
    public void run() {
      while (running) {
        terrain.iterate();
        long checksum = terrain.getChecksum();
        if (historySet.contains(checksum)) {
          Platform.runLater(MainController.this::stop);
        } else {
          historySet.add(checksum);
          historyList.add(checksum);
          if (historyList.size() > HISTORY_LENGTH) {
            historySet.remove(historyList.remove(0));
          }
        }
      }
      Platform.runLater(MainController.this::updateDisplay);
      Platform.runLater(MainController.this::updateControls);
    }

  }

  private class Updater extends AnimationTimer {

    @Override
    public void handle(long now) {
      updateDisplay();
    }

  }

}
