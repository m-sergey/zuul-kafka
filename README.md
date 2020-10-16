# PoC Transform REST to Kafka message (Request-Response)

## Run

### Start Kafka
> docker-compose up -d

### Start Eureka server

> gradle :eureka-server:bootRun

### Start Sample service

> gradle :sample-service:bootRun


### Start Zuul gateway

> gradlew :zuul-gateway:bootRun

## Test requests

## GET 
> curl -d "Sergey" -X POST http://localhost:8662/test/hello

Who are you?

## Request 
> curl -d "Sergey" -X POST http://localhost:8662/test/hello

Hello, Sergey
