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
package io.camunda.connector.impl.inbound.correlation;

import io.camunda.connector.impl.inbound.ProcessCorrelationPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Properties of a message published by an Inbound Connector */
public class MessageCorrelationPoint extends ProcessCorrelationPoint {

  private static final Logger LOG = LoggerFactory.getLogger(MessageCorrelationPoint.class);
  private final String messageName;

  public MessageCorrelationPoint(String messageName) {
    this.messageName = messageName;
    LOG.debug("Created inbound correlation point: " + this);
  }

  public String getMessageName() {
    return messageName;
  }

  @Override
  public String getId() {
    return messageName;
  }

  @Override
  public String toString() {
    return "MessageCorrelationPoint{" + "messageName='" + messageName + '\'' + '}';
  }

  @Override
  public int compareTo(ProcessCorrelationPoint o) {
    if (!this.getClass().equals(o.getClass())) {
      return 1;
    }
    MessageCorrelationPoint other = (MessageCorrelationPoint) o;
    return messageName.compareTo(other.messageName);
  }
}
