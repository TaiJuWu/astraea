/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.astraea.app.cost;

import java.util.Collection;

/** used to aggregate a sequence into a number */
public interface Dispersion {
  /**
   * use correlation coefficient to a aggregate a sequence * @return correlation coefficient
   * Dispersion
   */
  static Dispersion correlationCoefficient() {
    return brokerCost -> {
      var dataRateMean = brokerCost.stream().mapToDouble(x -> x).sum() / brokerCost.size();
      if (dataRateMean == 0) return 0;
      var dataRateSD =
          Math.sqrt(
              brokerCost.stream().mapToDouble(score -> Math.pow((score - dataRateMean), 2)).sum()
                  / brokerCost.size());
      return dataRateSD / dataRateMean;
    };
  }

  /**
   * aggregated the values into a number
   *
   * @param scores origin data
   * @return aggregated data
   */
  double calculate(Collection<Double> scores);
}