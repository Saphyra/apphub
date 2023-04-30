NAMESPACE=${1:-$(git rev-parse --abbrev-ref HEAD)}
SERVER_PORT=${2:-8100}

./infra/deployment/script/release_port.sh $SERVER_PORT
kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n "$NAMESPACE" &