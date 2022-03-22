NAMESPACE_NAME=${1:-develop}
SCRIPT_DIR_NAME=${2:-develop}
MAX_WAIT_TIME=${3:60}

function deployByDirectory() {
  echo ""
  echo "Deploying services defined in directory $1"

  for file in $1; do
    echo ""
    echo "$file";
    SERVICE_NAME="$(basename "$file" .yml)"

    kubectl -n "$NAMESPACE_NAME" delete deployment "$SERVICE_NAME"
    kubectl -n "$NAMESPACE_NAME" delete service "$SERVICE_NAME"
    kubectl apply -n "$NAMESPACE_NAME" -f "$file"
  done

  ./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" "$MAX_WAIT_TIME" 2 10
  STARTUP_RESULT=$?
  if [[ "$STARTUP_RESULT" -ne 0 ]]; then
    echo "Services failed to start."
    exit 1
  fi
}

echo "Deploying to namespace $NAMESPACE_NAME with scriptDirName $SCRIPT_DIR_NAME"

echo ""
kubectl create namespace "$NAMESPACE_NAME"
echo ""

kubectl -n "$NAMESPACE_NAME" scale deployments --replicas=0 --all
./infra/deployment/script/setup_namespace.sh "$NAMESPACE_NAME"
sleep 5

deployByDirectory "./infra/deployment/service/$SCRIPT_DIR_NAME/platform/*"
deployByDirectory "./infra/deployment/service/$SCRIPT_DIR_NAME/service/*"
