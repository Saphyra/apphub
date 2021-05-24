NAMESPACE_NAME=${1:-develop}

./scale.sh "$NAMESPACE_NAME" 0
./scale.sh "$NAMESPACE_NAME" 1

sleep 5

./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 60 2
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  exit 1
fi

start ./port_forward.sh "$NAMESPACE_NAME"