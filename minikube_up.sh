minikube start && start minikube dashboard

NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)

./scale.sh production 0
./scale.sh develop 0
./scale.sh "$NAMESPACE_NAME" 0

./infra/deployment/script/wait_for_pods_ready.sh production 180 1 5
./scale.sh production 1
./infra/deployment/script/wait_for_pods_ready.sh production 180 5 20
./pp.sh

./infra/deployment/script/wait_for_pods_ready.sh develop 60 1 5
./scale.sh develop 1
./infra/deployment/script/wait_for_pods_ready.sh develop 60 5 20

if [[ "$NAMESPACE" != "develop" ]]; then
  ./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 60 1 5
  ./scale.sh "$NAMESPACE_NAME" 1
  ./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 60 5 20
fi

start ./port_forward.sh "$NAMESPACE_NAME"