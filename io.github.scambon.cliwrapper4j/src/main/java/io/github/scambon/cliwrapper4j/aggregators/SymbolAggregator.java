/* Copyright 2018 Sylvain Cambon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.scambon.cliwrapper4j.aggregators;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An aggregator that uses the parameter as a symbol between the command or option and the flattened
 * value.
 */
public final class SymbolAggregator implements IAggregator {

  @Override
  public String aggregate(String commandOrOption, String value, String separator) {
    return Arrays.asList(commandOrOption, value)
        .stream()
        .filter(element -> element != null && !element.isEmpty())
        .collect(Collectors.joining(separator));
  }
}