NAMESPACE_NAME=${1:-develop}
REPLICAS=${2:-1}

echo "Scaling all pods to $REPLICAS for namespace $NAMESPACE_NAME"

kubectl -n "$NAMESPACE_NAME" scale deployments --replicas="$REPLICAS" --all