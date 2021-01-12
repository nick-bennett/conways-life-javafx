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
package edu.cnm.deepdive;

/**
 * Proxy entry point for JAR-packaged JavaFX application. The {@link #main(String[])} method of this
 * class simply invokes {@link Life#main(String[])}.
 */
public class Launcher {

  /**
   * Launches application by invoking {@link Life#main(String[])} with any command-line arguments.
   *
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    Life.main(args);
  }

}
