minikube start && start minikube dashboard

./scale.sh production 0
./scale.sh develop 0

./infra/deployment/script/wait_for_pods_ready.sh production 180 1 20
./scale.sh production 1
./infra/deployment/script/wait_for_pods_ready.sh production 180 5 20
./pp.sh

./infra/deployment/script/wait_for_pods_ready.sh develop 60 1 20
./scale.sh develop 1
./infra/deployment/script/wait_for_pods_ready.sh develop 60 5 20

NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)
./scale.sh "$NAMESPACE_NAME" 1
./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 60 5 20

start ./port_forward.sh "$NAMESPACE_NAME"