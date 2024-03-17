NAMESPACE_NAME=${1:-$(git rev-parse --abbrev-ref HEAD)}
HEADLESS=${2:-true}
DISABLED_GROUPS=${3:-headed-only}
SERVER_PORT=${4:-8070}
DATABASE_PORT=${5:-8071}
DATABASE_NAME=${6:-postgres}
INTEGRATION_SERVER_PORT=${7:-8072}
THREAD_COUNT=${8:-6}

echo "Running Integration tests against namespace $NAMESPACE_NAME"

./infra/deployment/script/start_integration_server.sh $INTEGRATION_SERVER_PORT

start ./port_forward.sh $NAMESPACE_NAME $SERVER_PORT $DATABASE_PORT

cd apphub-integration || exit
mvn -DthreadCount="$THREAD_COUNT" -DargLine="-DthreadCount=$THREAD_COUNT -DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -DdatabaseName=$DATABASE_NAME -Dheadless=$HEADLESS -DretryEnabled=true -DrestLoggingEnabled=false -DdisabledGroups=$DISABLED_GROUPS -DintegrationServerEnabled=true" clean test

cd .. || exit

./infra/deployment/script/release_port.sh $SERVER_PORT
./infra/deployment/script/release_port.sh $DATABASE_PORT

taskkill //F //IM chromedriver.exe //T