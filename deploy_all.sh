DIRNAME=$PWD
echo "Dirname: $DIRNAME"

if [ "$1" != "skipBuild" ]; then
  mvn -T 16 clean install
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi

NAMESPACE_NAME="a-test-"$RANDOM
./deploy.sh "$NAMESPACE_NAME"

PORT=$RANDOM

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 60 10
STARTUP_RESULT=$?
if  [[ "$STARTUP_RESULT" -ne 0 ]] ; then
  echo "Services failed to start."
  kubectl delete namespace $NAMESPACE_NAME
  exit 1;
fi

trap "exit" INT TERM ERR
trap "kill 0" EXIT

kubectl port-forward deployment/main-gateway $PORT:8080 -n $NAMESPACE_NAME &

mvn -T 16 clean test --activate-profiles integration
TEST_RESULT=$?
if  [[ "$TEST_RESULT" -ne 0 ]] ; then
  echo "Tests failed"
  kubectl delete namespace $NAMESPACE_NAME
  exit 1;
else
  echo "Tests passed successfully"
fi
kubectl delete namespace $NAMESPACE_NAME