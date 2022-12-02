NAMESPACE=${1:-$(git rev-parse --abbrev-ref HEAD)}

echo "Forwarding to namespace $NAMESPACE"

./infra/deployment/script/release_port.sh 9001
./infra/deployment/script/release_port.sh 9002

kubectl port-forward deployment/main-gateway 9001:8080 -n "$NAMESPACE" &
kubectl port-forward deployment/postgres 9002:5432 -n "$NAMESPACE" &