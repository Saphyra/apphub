trap "exit" INT TERM ERR
trap "kill 0" EXIT

NAMESPACE_NAME=${1:-develop}

SERVER_PORT=$RANDOM
DATABASE_PORT=$RANDOM
kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n "$NAMESPACE_NAME" &
kubectl port-forward deployment/postgres $DATABASE_PORT:5432 -n "$NAMESPACE_NAME" &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=true" clean test
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  exit 1
else
  echo "Tests passed successfully"
fi

taskkill //F //IM chromedriver.exe //T