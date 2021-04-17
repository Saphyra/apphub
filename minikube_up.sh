minikube start && start minikube dashboard

./scale.sh production 1
sleep 20
./infra/deployment/script/wait_for_pods_ready.sh production 60 5

./scale.sh develop 1
sleep 20
./infra/deployment/script/wait_for_pods_ready.sh develop 60 5

./pp.sh
start ./port_forward.sh develop