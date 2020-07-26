trap "exit" INT TERM ERR
trap "kill 0" EXIT

SERVER_PORT=$RANDOM
kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n production &

cd ./apphub-proxy || exit
mvn clean package

java -Dforwarded_port=$SERVER_PORT -jar target/proxy.jar