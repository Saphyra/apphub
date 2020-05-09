NAMESPACE=${1:-default}
kubectl port-forward deployment/main-gateway 9001:8080 -n "$NAMESPACE" &

