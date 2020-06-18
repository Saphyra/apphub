NAMESPACE=${1:-develop}
kubectl port-forward deployment/main-gateway 9001:8080 -n "$NAMESPACE" &
kubectl port-forward deployment/postgres 9002:5432 -n "$NAMESPACE" &