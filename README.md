# inbound-prototype

A prototype of REST ("webhook") inbound connector ([ref](https://github.com/camunda/product-hub/issues/174)).


## Start C8 Environment

```bash
docker compose up --wait
```

Note: you can also use Camunda SaaS instead of spinning up an environment locally

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
# working process { context=GITHUB_INBOUND }
zbctl --insecure deploy resource example/pull-request-notification.bpmn

# broken process { context=GITHUB_INBOUND_BROKEN }
zbctl --insecure deploy resource example/broken.bpmn
```

## Start a process


```bash
# webhook that activates
curl -XPOST -H 'Content-Type: application/json' -H "X-Hub-Signature: secrets.GITHUB_INBOUND_SECRET" localhost:8080/inbound/GITHUB_INBOUND  --data @example/webhook-payload-activates.json

# webhook without secret
curl -XPOST -H 'Content-Type: application/json' localhost:8080/inbound/GITHUB_INBOUND  --data @example/webhook-payload-activates.json

# webhook that is ignored (wrong type)
curl -XPOST -H 'Content-Type: application/json' -H "X-Hub-Signature: secrets.GITHUB_INBOUND_SECRET" localhost:8080/inbound/GITHUB_INBOUND  --data @example/webhook-payload-ignored.json

# webhook that reports an error (broken activation condition)
curl -XPOST -H 'Content-Type: application/json' -H "X-Hub-Signature: secrets.GITHUB_INBOUND_SECRET" localhost:8080/inbound/GITHUB_INBOUND_BROKEN  --data @example/webhook-payload-ignored.json
```
