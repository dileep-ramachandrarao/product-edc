#!/bin/bash

# UNINSTALL
helm uninstall edc-all-in-one --namespace edc-all-in-one
sleep 10

#kubectl delete pvc -n edc-all-in-one --all
#kubectl delete pv -n edc-all-in-one --all

# DELETE IMAGES
docker image rm edc-controlplane-postgresql-hashicorp-vault:latest
docker image rm edc-controlplane-postgresql-hashicorp-vault:0.0.5-SNAPSHOT
docker image rm edc-dataplane-hashicorp-vault:latest
docker image rm edc-dataplane-hashicorp-vault:0.0.5-SNAPSHOT
minikube image rm edc-controlplane-postgresql-hashicorp-vault:latest
minikube image rm edc-controlplane-postgresql-hashicorp-vault:0.0.5-SNAPSHOT
minikube image rm edc-dataplane-hashicorp-vault:latest
minikube image rm edc-dataplane-hashicorp-vault:0.0.5-SNAPSHOT

# CREATE NEW IMAGE
./mvnw spotless:apply clean package -Pwith-docker-image

# LOAD IMAGE
minikube image load edc-controlplane-postgresql-hashicorp-vault:latest
minikube image load edc-dataplane-hashicorp-vault:latest
minikube image ls | grep edc

# INSTALL
helm install edc-all-in-one --namespace edc-all-in-one --create-namespace edc-tests/src/main/resources/deployment/helm/all-in-one
kubectl get pods -n edc-all-in-one
