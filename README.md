# inbound-prototype

A prototype of REST ("webhook") inbound connector ([ref](https://github.com/camunda/product-hub/issues/174)).


## Start C8 Environment

**WARNING**: Right now the C8 env is using SNAPSHOT versions, which might break, and we should update this to 8.1.0-alpha5 once it is available.

```bash
docker compose up --wait
```

URLs:

- Zeebe: 'grpc://localhost:26500'
- Operate: `http://localhost:8081`
- Elasticsearch: `http://localhost:9200`

## Start Inbound Connector

```bash
mvn spring-boot:run
```

## Deploy process

```bash
zbctl --insecure deploy resource example/pull-request-notification.bpmn
```

## Start a process


```bash
curl -XPOST -H 'Content-Type: application/json' localhost:8080/inbound/GITHUB_INBOUND  -d '{}'
```
