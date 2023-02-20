minikube start && start minikube dashboard

NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)

./scale.sh production 0
./scale.sh develop 0
./scale.sh "$NAMESPACE_NAME" 0

./infra/deployment/script/deploy.sh production production 180
./pp.sh

./infra/deployment/script/deploy.sh develop

if [[ "$NAMESPACE" != "develop" ]]; then
  ./infra/deployment/script/deploy.sh "$NAMESPACE_NAME"
fi

start ./port_forward.sh "$NAMESPACE_NAME"