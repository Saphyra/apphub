NAMESPACE_NAME=${1:-$(git rev-parse --abbrev-ref HEAD)}
HEADLESS=${2:-true}
DISABLED_GROUPS=${3:-}

echo "Running Integration tests against namespace $NAMESPACE_NAME"

SERVER_PORT=$RANDOM
DATABASE_PORT=$RANDOM
start kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n "$NAMESPACE_NAME"
start kubectl port-forward deployment/postgres $DATABASE_PORT:5432 -n "$NAMESPACE_NAME"

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=$HEADLESS -DretryEnabled=true -DrestLoggingEnabled=false -DdisabledGroups=$DISABLED_GROUPS" clean test
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  cd .. || exit

  ./release_port.sh $SERVER_PORT
  ./release_port.sh $DATABASE_PORT

  taskkill //F //IM chromedriver.exe //T
  exit
else
  echo "Tests passed successfully"
fi

cd .. || exit

./release_port.sh $SERVER_PORT
./release_port.sh $DATABASE_PORT

taskkill //F //IM chromedriver.exe //T