DIRNAME=$PWD
echo "Dirname: $DIRNAME"

if [ "$1" != "skipBuild" ]; then
  mvn -T 4 clean install
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi

#Deploying to test

NAMESPACE_NAME="a"$RANDOM
./deploy.sh "$NAMESPACE_NAME"

PORT=$RANDOM

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 12 10
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  kubectl delete namespace $NAMESPACE_NAME
  exit 1
fi

trap "exit" INT TERM ERR
trap "kill 0" EXIT

kubectl port-forward deployment/main-gateway $PORT:8080 -n $NAMESPACE_NAME &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$PORT -Dheadless=true" clean test
cd ../ || exit
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  kubectl delete namespace $NAMESPACE_NAME
  exit 1
else
  echo "Tests passed successfully"
fi
kubectl delete namespace $NAMESPACE_NAME &

#Deploying to default
NAMESPACE_NAME="default"
./deploy.sh "$NAMESPACE_NAME"

PORT=$RANDOM

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 12 10
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  kubectl delete namespace $NAMESPACE_NAME
  exit 1
fi

kubectl port-forward deployment/main-gateway $PORT:8080 -n $NAMESPACE_NAME &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$PORT -Dheadless=true" clean test
cd ../ || exit
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  exit 1
else
  echo "Tests passed successfully"
fi

echo "Deployment finished."
