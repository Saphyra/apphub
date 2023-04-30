NAMESPACE_NAME=${1:-$(git rev-parse --abbrev-ref HEAD)}
HEADLESS=${2:-true}
DISABLED_GROUPS=${3:-}

echo "Running Integration tests against namespace $NAMESPACE_NAME"

SERVER_PORT=8100
DATABASE_PORT=8101
start ./port_forward.sh $NAMESPACE_NAME $SERVER_PORT $DATABASE_PORT

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=$HEADLESS -DretryEnabled=true -DrestLoggingEnabled=false -DdisabledGroups=$DISABLED_GROUPS" clean test
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  cd .. || exit

  ./infra/deployment/script/release_port.sh $SERVER_PORT
  ./infra/deployment/script/release_port.sh $DATABASE_PORT

  taskkill //F //IM chromedriver.exe //T
  exit
else
  echo "Tests passed successfully"
fi

cd .. || exit

./infra/deployment/script/release_port.sh $SERVER_PORT
./infra/deployment/script/release_port.sh $DATABASE_PORT

taskkill //F //IM chromedriver.exe //T