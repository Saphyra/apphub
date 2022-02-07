minikube start && start minikube dashboard

./scale.sh production 1
./infra/deployment/script/wait_for_pods_ready.sh production 180 5 20
./pp.sh

./scale.sh develop 1
./infra/deployment/script/wait_for_pods_ready.sh develop 60 5 20

start ./port_forward.sh develop