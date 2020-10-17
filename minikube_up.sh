minikube start && start minikube dashboard

kubectl -n production scale deployments --all --replicas=0
kubectl -n production scale deployments --all --replicas=1

sleep 20

./pp.sh