NAMESPACE_NAME=${1:-develop}
HEADLESS=${2:-true}

SERVER_PORT=$RANDOM
DATABASE_PORT=$RANDOM
nohup kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n "$NAMESPACE_NAME" &
nohup kubectl port-forward deployment/postgres $DATABASE_PORT:5432 -n "$NAMESPACE_NAME" &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=$HEADLESS -DpreCreateDrivers=true -DretryEnabled=true -DrestLoggingEnabled=false" clean test
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
else
  echo "Tests passed successfully"
fi

cd .. || exit

./release_port.sh $SERVER_PORT
./release_port.sh $DATABASE_PORT

taskkill //F //IM chromedriver.exe //T