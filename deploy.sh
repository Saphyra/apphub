NAMESPACE_NAME=${1:-develop}

echo ""
kubectl create namespace "$NAMESPACE_NAME"
echo ""
./infra/deployment/script/setup_namespace.sh "$NAMESPACE_NAME"

SCRIPT_DIRECTORY_NAME="./infra/deployment/service/$NAMESPACE_NAME/*"
for file in $SCRIPT_DIRECTORY_NAME; do
  echo ""
  echo "$file";
  SERVICE_NAME="$(basename "$file" .yml)"

  kubectl -n "$NAMESPACE_NAME" delete deployment "$SERVICE_NAME"
  kubectl -n "$NAMESPACE_NAME" delete service "$SERVICE_NAME"
  kubectl apply -n "$NAMESPACE_NAME" -f "$file"
done

sleep 10