/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
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
package io.camunda.connector.impl.outbound;

import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import java.util.Arrays;
import java.util.Objects;

public class OutboundConnectorConfiguration {

  private final String name;
  private final String type;
  private final String[] inputVariables;

  private final Class<? extends OutboundConnectorFunction> connectorClass;

  public OutboundConnectorConfiguration(
      final String name,
      final String[] inputVariables,
      final String type,
      final Class<? extends OutboundConnectorFunction> connectorClass) {
    this.name = name;
    this.type = type;
    this.inputVariables = inputVariables;
    this.connectorClass = connectorClass;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String[] getInputVariables() {
    return inputVariables;
  }

  public Class<? extends OutboundConnectorFunction> getConnectorClass() {
    return connectorClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OutboundConnectorConfiguration that = (OutboundConnectorConfiguration) o;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public String toString() {
    return "OutboundConnectorConfiguration{"
        + "name='"
        + name
        + '\''
        + ", type='"
        + type
        + '\''
        + ", inputVariables="
        + Arrays.toString(inputVariables)
        + '}';
  }
}
