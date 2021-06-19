NAMESPACE=$1

echo ""
echo "Setting up namespace $NAMESPACE"
echo ""

kubectl apply -n "$NAMESPACE" -f infra/deployment/persistent-volume.yaml
kubectl apply -n "$NAMESPACE" -f infra/deployment/deploy-postgres.yaml
