eval "$(minikube docker-env)"

NAMESPACE_NAME=${1:-$(git rev-parse --abbrev-ref HEAD)}
DIRECTORY=${2:-develop}

echo "Deploying frontend to namespace $NAMESPACE_NAME from script dir $DIRECTORY"

./infra/deployment/script/build_frontend.sh

kubectl -n "$NAMESPACE_NAME" delete deployment frontend
kubectl -n "$NAMESPACE_NAME" delete service frontend
kubectl apply -n "$NAMESPACE_NAME" -f "./infra/deployment/service/$DIRECTORY/01_platform/frontend.yml"

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 60 1