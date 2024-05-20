NAMESPACE=${1:-$(git rev-parse --abbrev-ref HEAD)}
SERVER_PORT=${2:-9001}
DATABASE_PORT=${3:-9002}

echo "Forwarding to namespace $NAMESPACE with serverPort $SERVER_PORT and databasePort $DATABASE_PORT"

if [[ "$DATABASE_PORT" != 5432 ]]; then
  ./infra/deployment/script/release_port.sh $DATABASE_PORT
  kubectl port-forward deployment/postgres $DATABASE_PORT:5432 -n "$NAMESPACE" &
fi

./infra/deployment/script/release_port.sh $SERVER_PORT
kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n "$NAMESPACE" &