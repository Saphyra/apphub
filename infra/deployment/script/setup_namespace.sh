NAMESPACE=$1

echo ""
echo "Setting up namespace $NAMESPACE"
echo ""

kubectl create namespace "$NAMESPACE"

kubectl apply -n "$NAMESPACE" -f infra/deployment/persistent-volume.yaml
kubectl apply -n "$NAMESPACE" -f infra/deployment/deploy-postgres.yaml

./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE" 60 1 5