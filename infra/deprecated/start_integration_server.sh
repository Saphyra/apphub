INTEGRATION_SERVER_PORT=${1:-8072}

./infra/deployment/script/release_port.sh $INTEGRATION_SERVER_PORT

cd ./apphub-integration-server

mvn clean package

echo "Starting integration server..."
start java -jar target/application.jar

echo "Pinging $INTEGRATION_SERVER_PORT"
curl -s -o nul --head -X GET --silent --retry 20 --retry-connrefused --retry-delay 1 localhost:$INTEGRATION_SERVER_PORT/health