DIRNAME=$PWD
echo "Dirname: $DIRNAME"

./infra/deployment/script/build.sh
rc=$?
if [[ "$rc" -ne 0 ]]; then
  echo 'Build failed.'
  exit 1
fi

NAMESPACE_NAME="a-test-"$RANDOM
#NAMESPACE_NAME="test-0"
#NAMESPACE_NAME="default"
echo ""
kubectl create namespace $NAMESPACE_NAME
echo ""
./infra/deployment/script/setup_namespace.sh "$NAMESPACE_NAME"

for file in ./infra/deployment/service/*; do
  echo ""
  SERVICE_NAME="$(basename "$file" .yml)"

  kubectl -n "$NAMESPACE_NAME" delete deployment "$SERVICE_NAME"
  kubectl -n "$NAMESPACE_NAME" delete service "$SERVICE_NAME"
  kubectl apply -n "$NAMESPACE_NAME" -f "$file"
done
