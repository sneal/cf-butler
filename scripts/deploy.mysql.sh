#!/usr/bin/env bash

# Script assumes MySQL (https://network.pivotal.io/products/pivotal-mysql/) is available as service in cf marketplace
# Feel free to swap out the service for other MySQL providers, like:
#   * Meta Azure Service Broker - https://github.com/Azure/meta-azure-service-broker/blob/master/docs/azure-mysql-db.md
#   * AWS Service Broker - http://docs.pivotal.io/aws-services/creating.html#rds

set -e

export APP_NAME=cf-butler


cf push --no-start
cf create-service credhub default $APP_NAME-secrets -c config/secrets.json
cf create-service p.mysql db-small $APP_NAME-backend
while [[ $(cf service $APP_NAME-secrets) != *"succeeded"* ]]; do
  echo "$APP_NAME-secrets is not ready yet..."
  sleep 5
done
cf bind-service $APP_NAME $APP_NAME-secrets
while [[ $(cf service $APP_NAME-backend) != *"succeeded"* ]]; do
  echo "$APP_NAME-backend is not ready yet..."
  sleep 5
done
cf bind-service $APP_NAME $APP_NAME-backend
cf start $APP_NAME
