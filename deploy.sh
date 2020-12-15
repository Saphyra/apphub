NAMESPACE_NAME=${1:-develop}

echo ""
kubectl create namespace "$NAMESPACE_NAME"
echo ""

kubectl -n "$NAMESPACE_NAME" scale deployments --replicas=0 --all

sleep 5
./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 30 1

./infra/deployment/script/setup_namespace.sh "$NAMESPACE_NAME"

SCRIPT_DIRECTORY_NAME="./infra/deployment/service/$NAMESPACE_NAME/*"
for file in $SCRIPT_DIRECTORY_NAME; do
  echo ""
  echo "$file";
  SERVICE_NAME="$(basename "$file" .yml)"

  kubectl -n "$NAMESPACE_NAME" delete deployment "$SERVICE_NAME"
  kubectl -n "$NAMESPACE_NAME" delete service "$SERVICE_NAME"
  kubectl apply -n "$NAMESPACE_NAME" -f "$file"
done

echo "Waiting for pods to start..."
sleep 20