trap "exit" INT TERM ERR
trap "kill 0" EXIT

PORT=$RANDOM
NAMESPACE_NAME=${1:-default}

kubectl port-forward deployment/main-gateway $PORT:8080 -n "$NAMESPACE_NAME" &
cd apphub-integration || exit
mvn -DargLine="-DserverPort=$PORT" clean test
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  exit 1
else
  echo "Tests passed successfully"
fi
