NAMESPACE_NAME=${1:-develop}
SCRIPT_DIR_NAME=${2:-develop}

echo "Deploying to namespace $NAMESPACE_NAME with scriptDirName $SCRIPT_DIR_NAME"

echo ""
kubectl create namespace "$NAMESPACE_NAME"
echo ""

#kubectl -n "$NAMESPACE_NAME" scale deployments --replicas=0 --all
#sleep 5
#./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 30 1

./infra/deployment/script/setup_namespace.sh "$NAMESPACE_NAME"

SCRIPT_DIRECTORY_NAME="./infra/deployment/service/$SCRIPT_DIR_NAME/*"
for file in $SCRIPT_DIRECTORY_NAME; do
  echo ""
  echo "$file";
  SERVICE_NAME="$(basename "$file" .yml)"

  kubectl -n "$NAMESPACE_NAME" delete deployment "$SERVICE_NAME"
  kubectl -n "$NAMESPACE_NAME" delete service "$SERVICE_NAME"
  kubectl apply -n "$NAMESPACE_NAME" -f "$file"
done

echo ""
echo "Waiting for pods to start..."
sleep 10

./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 60 2
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  exit 1
fi