minikube start && start minikube dashboard

./scale.sh production 1
sleep 20
./infra/deployment/script/wait_for_pods_ready.sh production 180 5
./pp.sh

./scale.sh develop 1
sleep 20
./infra/deployment/script/wait_for_pods_ready.sh develop 60 5

start ./port_forward.sh develop