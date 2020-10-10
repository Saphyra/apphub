trap "exit" INT TERM ERR
trap "kill 0" EXIT

SERVER_PORT=$RANDOM
NAMESPACE_NAME=production

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 12 10

kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n $NAMESPACE_NAME &

cd ./apphub-proxy || exit
mvn clean package

java -Dforwarded_port=$SERVER_PORT -jar target/proxy.jar