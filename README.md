> This is in developer preview and can be subject to breaking changes.

# Camunda Connector SDK

[![CI](https://github.com/camunda/connector-sdk/actions/workflows/CI.yml/badge.svg)](https://github.com/camunda/connector-sdk/actions/workflows/CI.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.camunda.connector/connector-core/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.camunda.connector/connector-core)
[![Connector Template](https://img.shields.io/badge/template%20repository-use-blue)](https://github.com/camunda/connector-template)

The Connector SDK allows you to [develop custom Camunda 8 Connectors](https://docs.camunda.io/docs/components/integration-framework/introduction-to-connectors/#connectors) in Java.

You can focus on the logic of the Connector, test it locally, and reuse its runtime logic in multiple environments. The SDK achieves this by abstracting from Camunda Platform 8 internals that usually come with [job workers](https://docs.camunda.io/docs/components/concepts/job-workers/).

Head over to our [**Connector Template**](https://github.com/camunda/connector-template) for a head start.

## Contents

* [Create a Connector](#create-a-connector)
* [Connector Validation](#connector-validation)
* [Start a Connector](#start-a-connector)
* [Build](#build)
* [Build a release](#build-a-release)

## Create a Connector

Include the [connector core](./core) via maven:

```xml

<dependency>
  <groupId>io.camunda.connector</groupId>
  <artifactId>connector-core</artifactId>
  <version>0.2.2</version>
</dependency>
```

Define your connector logic through the [`OutboundConnectorFunction`](./core/src/main/java/io/camunda/connector/api/outbound/OutboundConnectorFunction.java) interface:

```java

@OutboundConnector(
    name = "PING",
    inputVariables = {"caller"},
    type = "io.camunda.example.PingConnector:1"
)
public class PingConnector implements OutboundConnectorFunction {

  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {

    var request = context.getVariablesAsType(PingRequest.class);

    context.replaceSecrets(request);

    var caller = request.getCaller();

    return new PingResponse("Pong to " + caller);
  }
}
```

Expose your connector as an [`OutboundConnectorFunction` SPI implementation](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html).

### Next steps

* Add [Input validation](#connector-validation) to your Connector

## Connector Validation

If you want to validate your Connector input, the SDK provides a default implementation using [Jakarta Bean Validation](https://beanvalidation.org/) with the [connector-validation](./validation) module. You can include it via maven with the following dependency:

```xml

<dependency>
  <groupId>io.camunda.connector</groupId>
  <artifactId>connector-validation</artifactId>
  <version>0.2.2</version>
</dependency>
```

Find more details in the [validation module](./validation).

## Start a Connector

Spin up your connector as a [job worker](./runtime-job-worker) or [build your own run-time](./runtime-util), tailored towards your environment.

## Build

```bash
mvn clean package
```

## Build a release

Trigger the [release action](https://github.com/camunda/connector-sdk/actions/workflows/RELEASE.yml) manually with the version `x.y.z` you want to release.
This can be done on the `main` branch as well as `stable/.x.y` maintenance branches. You can choose the branch to execute the action on as described in the
[GitHub documentation](https://docs.github.com/en/actions/managing-workflow-runs/manually-running-a-workflow).

When triggered from the `main` branch, a maintenance branch `stable/x.y` will be created based on the release version `x.y.z` that you specified.

**Note**: This currently also applies for further classifiers like `x.y.z-rc1` or `x.y.z-alpha1`, i.e. a new branch `stable/x.y` will be created for that release. If the branch to create already exists, the release will fail.
