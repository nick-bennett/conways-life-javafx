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
package edu.cnm.deepdive.view;

import edu.cnm.deepdive.model.Terrain;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Implements a simple bitmap display of a Conway's Game of Life CA, enhanced for shading of active
 * cells based on age (number of consecutive iterations that cell has been in the active state).
 */
public class TerrainView extends Canvas {

  private static final double MAX_HUE = 360;
  private static final double MAX_SATURATION = 1;
  private static final double MAX_BRIGHTNESS = 1;
  private static final double DEFAULT_HUE = 120;
  private static final double DEFAULT_SATURATION = 1;
  private static final double DEFAULT_NEW_BRIGHTNESS = 1;
  private static final double DEFAULT_OLD_BRIGHTNESS = 0.5;

  private double hue = DEFAULT_HUE;
  private double saturation = DEFAULT_SATURATION;
  private double newBrightness = DEFAULT_NEW_BRIGHTNESS;
  private double oldBrightness = DEFAULT_OLD_BRIGHTNESS;
  private boolean colorsUpdated;
  private byte[][] cells;
  private Color[] cellColors;
  private Color backgroundColor;
  private Terrain terrain;
  private WritableImage buffer;
  private PixelWriter writer;
  private boolean bound;

  @Override
  public boolean isResizable() {
    if (!bound) {
      widthProperty().bind(((Pane) getParent()).widthProperty());
      heightProperty().bind(((Pane) getParent()).heightProperty());
      bound = true;
    }
    return true;
  }

  @Override
  public void resize(double width, double height) {
    super.resize(width, height);
    if (terrain != null) {
      draw();
    }
  }

  /**
   * Sets the {@link Terrain} instance to be used as the source for this view.
   */
  public void setTerrain(Terrain terrain) {
    this.terrain = terrain;
    int size = terrain.getSize();
    cells = new byte[size][size];
    buffer = new WritableImage(size, size);
    writer = buffer.getPixelWriter();
    colorsUpdated = false;
  }

  /**
   * Returns the hue used for display of active cells.
   */
  public double getHue() {
    return hue;
  }

  /**
   * Sets the hue to use for display of active cells.
   */
  public void setHue(double hue) {
    this.hue = Math.max(0, Math.min(MAX_HUE, hue));
    colorsUpdated = false;
  }

  /**
   * Returns the HSV saturation used for display of active cells.
   */
  public double getSaturation() {
    return saturation;
  }

  /**
   * Sets the HSV saturation to use for displat of active cells.
   */
  public void setSaturation(double saturation) {
    this.saturation = Math.max(0, Math.min(MAX_SATURATION, saturation));
    colorsUpdated = false;
  }

  /**
   * Returns the HSV brightness (value) used for display of newly active cells.
   */
  public double getNewBrightness() {
    return newBrightness;
  }

  /**
   * Sets the HSV brightness (value) to use for display of newly active cells.
   */
  public void setNewBrightness(double newBrightness) {
    this.newBrightness = Math.max(0, Math.min(MAX_BRIGHTNESS, newBrightness));
    colorsUpdated = false;
  }

  /**
   * Returns the HSV brightness (value) used for display of cells that have been active for at least
   * 127 iterations.
   */
  public double getOldBrightness() {
    return oldBrightness;
  }

  /**
   * Sets the HSV brightness (value) to use for display of cells that have been active for at least
   * 127 iterations.
   */
  public void setOldBrightness(double oldBrightness) {
    this.oldBrightness = Math.max(0, Math.min(MAX_BRIGHTNESS, oldBrightness));
    colorsUpdated = false;
  }

  /**
   * Draws all cells, shaded according to their ages. Each cell is rendered as a single pixel,
   * scaled according to the rendered size of this view.
   */
  public void draw() {
    if (buffer != null) {
      if (!colorsUpdated) {
        updateColors();
      }
      terrain.copyCells(cells);
      for (int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
        for (int colIndex = 0; colIndex < cells[rowIndex].length; colIndex++) {
          writer.setColor(colIndex, rowIndex,
              (cells[rowIndex][colIndex] > 0)
                  ? cellColors[cells[rowIndex][colIndex] - 1]
                  : backgroundColor
          );
        }
      }
      GraphicsContext context = getGraphicsContext2D();
      context.drawImage(buffer, 0, 0, cells.length, cells.length, 0, 0, getWidth(), getHeight());
    }
  }

  private void updateColors() {
    cellColors = new Color[Byte.MAX_VALUE];
    for (int i = 0; i < Byte.MAX_VALUE; i++) {
      cellColors[i] = Color.hsb(hue, saturation,
          oldBrightness + (newBrightness - oldBrightness) * (Byte.MAX_VALUE - i) / Byte.MAX_VALUE);
    }
    backgroundColor = Color.hsb(hue, 0, 0);
    colorsUpdated = true;
  }

}
