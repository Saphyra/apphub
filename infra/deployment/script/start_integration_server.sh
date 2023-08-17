./infra/deployment/script/release_port.sh 8072

cd ./apphub-integration-server

mvn clean package

echo "Starting integration server..."
start java -jar target/application.jar

echo "Pinging 8072"
curl -s -o nul --head -X GET --silent --retry 20 --retry-connrefused --retry-delay 1 localhost:8072/health