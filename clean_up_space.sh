eval "$(minikube docker-env)"
docker rmi -f $(docker images -a -q)