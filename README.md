> This is in developer preview and can be subject to breaking changes.

# Camunda Connector SDK

[![CI](https://github.com/camunda/connector-sdk/actions/workflows/CI.yml/badge.svg)](https://github.com/camunda/connector-sdk/actions/workflows/CI.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.camunda.connector/connector-core/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.camunda.connector/connector-core)
[![Connector Template](https://img.shields.io/badge/template%20repository-use-blue)](https://github.com/camunda/connector-template)

The Connector SDK allows you to [develop custom Camunda 8 Connectors](https://docs.camunda.io/docs/components/connectors/introduction-to-connectors/#connectors) in Java.

You can focus on the logic of the Connector, test it locally, and reuse its runtime logic in multiple environments. The SDK achieves this by abstracting from Camunda Platform 8 internals that usually come with [job workers](https://docs.camunda.io/docs/components/concepts/job-workers/).

Head over to our **template repositories** for a head start:
* [Outbound Connector Template](https://github.com/camunda/connector-template)
* [Inbound Connector Template](https://github.com/camunda/connector-template-inbound)

## Contents

* [Create a Connector](#create-a-connector)
  * [Outbound Connector](#outbound-connector)
  * [Inbound Connector](#inbound-connector)
* [Connector Validation](#connector-validation)
* [Start a Connector](#start-a-connector)
* [Build](#build)
* [Build a release](#build-a-release)

## Create a Connector

Include the [connector-core](./core), e.g. via Maven:

```xml
<dependency>
  <groupId>io.camunda.connector</groupId>
  <artifactId>connector-core</artifactId>
  <version>0.8.0</version>
  <scope>provided</scope>
</dependency>
```

Set the dependency to a `provided` scope as the runtimes that execute Connectors provide the necessary classes already.

### Outbound Connector

Define your Connector logic through the [`OutboundConnectorFunction`](./core/src/main/java/io/camunda/connector/api/outbound/OutboundConnectorFunction.java) interface:

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

### Inbound Connector

Define your Connector logic through the [`InboundConnectorExecutable`](./core/src/main/java/io/camunda/connector/api/inbound/InboundConnectorExecutable.java) interface:
```java
@InboundConnector(
    name = "SUBSCRIPTION",
    type = "io.camunda.example.SubscriptionConnector:1"
)
public class SubscriptionConnector implements InboundConnectorExecutable {

  private MockSubscription subscription; // imitates some real-world subscription

  @Override
  public void activate(InboundConnectorContext context) throws Exception {

    var properties = context.getPropertiesAsType(SubscriptionProperties.class);

    context.replaceSecrets(properties);
    context.validate(properties);

    // subscribe to events
    subscription = new MockSubscription(properties.getTopic());
    subscription.subscribe(event -> {
      context.correlate(event);
    });
  }

  @Override
  public void deactivate() throws Exception {
    // unsubscribe from events
    subscription.shutdown();
  }
}
```

### Connector Discovery

The SDK provides a default implementation for Connector discovery using [Java ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html) with the [connector-runtime-util](./runtime-util) module.

To make your Connector discoverable, expose the `OutboundConnectorFunction` or `InboundConnectorExecutable` implementation as an SPI implementation.
Alternatively, you can use the [manual discovery mechanism](https://docs.camunda.io/docs/self-managed/connectors-deployment/connectors-configuration/#manual-discovery-of-connectors) via properties.

## Connector Validation

If you want to validate your Connector input, the SDK provides a default implementation using [Jakarta Bean Validation](https://beanvalidation.org/) with the [connector-validation](./validation) module. You can include it via maven with the following dependency:

```xml

<dependency>
  <groupId>io.camunda.connector</groupId>
  <artifactId>connector-validation</artifactId>
  <version>0.8.0</version>
  <scope>provided</scope>
</dependency>
```

Set the dependency to a `provided` scope as the runtimes that execute Connectors provide the necessary classes already.

Find more details in the [validation module](./validation).

## Start a Connector

[Spring Zeebe](https://github.com/camunda-community-hub/spring-zeebe#run-outboundconnectors) Connector runtime supports running outbound Connectors as job workers and manages the lifecycle of the inbound Connectors.
You can also [build your own runtime](./runtime-util), tailored towards your environment.

## Build

```bash
mvn clean package
```

## Build a release

Trigger the [release action](https://github.com/camunda/connector-sdk/actions/workflows/RELEASE.yml) manually with the version `x.y.z` you want to release.
This can be done on the `main` branch as well as `stable/.x.y` maintenance branches. You can choose the branch to execute the action on as described in the
[GitHub documentation](https://docs.github.com/en/actions/managing-workflow-runs/manually-running-a-workflow).

When triggered from the `main` branch, a maintenance branch `stable/x.y` will be created based on the release version `x.y.z` that you specified.

### Pre-releases

If you apply further classifiers like `x.y.z-rc1` or `x.y.z-alpha1`, no maintenance branch will be created.
Furthermore, no commits will be pushed to the branch you release from. A tag will be created on a detached commit
that sets the release version on top of the current HEAD of the branch.
