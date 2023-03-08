minikube start && start minikube dashboard

NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)

./scale.sh production 0
./scale.sh develop 0
./scale.sh "$NAMESPACE_NAME" 0

function scaleUpByDirectory() {
  NAMESPACE=$1
  DIRECTORY=$2
  MAX_WAIT_TIME=$3
  echo ""
  echo "Scaling up services defined in directory $DIRECTORY for namespace $NAMESPACE with max wait time $MAX_WAIT_TIME"

  for file in $DIRECTORY; do
    echo ""
    echo "$file";
    SERVICE_NAME="$(basename "$file" .yml)"

    kubectl -n "$NAMESPACE" scale deployments --replicas=1 "$SERVICE_NAME"
  done

  ./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE" "$MAX_WAIT_TIME" 2 5
}

function scaleNamespace() {
    NAMESPACE=$1
    SCRIPT_DIR_NAME=$2

  ./infra/deployment/script/setup_namespace.sh $NAMESPACE
  scaleUpByDirectory $NAMESPACE "./infra/deployment/service/$SCRIPT_DIR_NAME/01_platform/*" 60
  scaleUpByDirectory $NAMESPACE "./infra/deployment/service/$SCRIPT_DIR_NAME/02_service/*" 180
}

scaleNamespace "production" "production"
./pp.sh

scaleNamespace "develop" "develop"

if [[ "$NAMESPACE_NAME" != "develop" ]]; then
  scaleNamespace "$NAMESPACE_NAME" "develop"
fi

start ./port_forward.sh "$NAMESPACE_NAME"