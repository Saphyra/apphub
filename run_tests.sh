trap "exit" INT TERM ERR
trap "kill 0" EXIT

PORT=$RANDOM
NAMESPACE_NAME="default"

kubectl port-forward deployment/main-gateway $PORT:8080 -n $NAMESPACE_NAME &
mvn -DargLine="-DserverPort=$PORT" -T 16 clean test -P integration
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  exit 1
else
  echo "Tests passed successfully"
fi
