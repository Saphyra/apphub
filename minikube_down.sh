./scale.sh production 0

NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)
./scale.sh "$NAMESPACE_NAME" 0

minikube stop