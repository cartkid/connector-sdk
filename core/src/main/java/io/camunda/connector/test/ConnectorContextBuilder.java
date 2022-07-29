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
package io.camunda.connector.test;

import io.camunda.connector.api.ConnectorContext;
import io.camunda.connector.api.ConnectorInput;
import io.camunda.connector.api.SecretProvider;
import io.camunda.connector.api.SecretStore;
import java.util.HashMap;
import java.util.Map;

public class ConnectorContextBuilder {

  protected Map<String, String> secrets = new HashMap<>();
  protected SecretProvider secretProvider = (name) -> secrets.get(name);

  protected String variablesAsJSON;

  private Object variablesAsObject;

  public static ConnectorContextBuilder create() {
    return new ConnectorContextBuilder();
  }

  private void assertNoVariables() {

    if (this.variablesAsJSON != null) {
      throw new IllegalStateException("variablesAsJSON already set");
    }

    if (this.variablesAsObject != null) {
      throw new IllegalStateException("variablesAsObject already set");
    }
  }

  /**
   * Provides the variables as a JSON string.
   *
   * @param variablesAsJSON
   */
  public ConnectorContextBuilder variables(String variablesAsJSON) {
    this.assertNoVariables();

    this.variablesAsJSON = variablesAsJSON;
    return this;
  }

  /**
   * Provides the variables as an object
   *
   * @param variablesAsObject
   */
  public ConnectorContextBuilder variables(Object variablesAsObject) {
    this.assertNoVariables();

    this.variablesAsObject = variablesAsObject;
    return this;
  }

  public ConnectorContextBuilder secret(String name, String value) {
    secrets.put(name, value);
    return this;
  }

  public ConnectorContextBuilder secrets(SecretProvider secretProvider) {
    this.secretProvider = secretProvider;
    return this;
  }

  public ConnectorContext build() {
    return new ConnectorContext() {

      private SecretStore secretStore;

      @Override
      public String getVariables() {

        if (variablesAsJSON == null) {
          throw new IllegalStateException("variablesAsJSON not provided");
        }

        return variablesAsJSON;
      }

      @Override
      public <T extends Object> T getVariablesAsType(Class<T> cls) {

        if (variablesAsObject == null) {
          throw new IllegalStateException("variablesAsObject not provided");
        }

        try {
          return cls.cast(variablesAsObject);
        } catch (ClassCastException ex) {
          throw new IllegalStateException(
              "no variablesAsObject of type " + cls.getName() + " provided", ex);
        }
      }

      @Override
      public void replaceSecrets(ConnectorInput input) {
        input.replaceSecrets(getSecretStore());
      }

      @Override
      public SecretStore getSecretStore() {
        if (secretStore == null) {
          secretStore = new SecretStore(secretProvider);
        }
        return secretStore;
      }
    };
  }
}
