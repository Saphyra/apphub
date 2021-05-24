NAMESPACE_NAME=${1:-develop}
REPLICAS=${2:-1}

kubectl -n "$NAMESPACE_NAME" scale deployments --replicas="$REPLICAS" --all