eval "$(minikube docker-env)"

while IFS="" read -r image_name || [ -n "$image_name" ]
do
  TRIMMED="$(sed -e 's/[[:space:]]*$//' <<<"${image_name}")"
  echo "$image_name"
  docker tag saphyra/"$TRIMMED":latest saphyra/"$TRIMMED":release
  docker push saphyra/"$TRIMMED":release &
done < ./infra/deployment/service/service_list

wait

#Deploying to production
NAMESPACE_NAME="production"
./deploy.sh "$NAMESPACE_NAME"

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 12 10
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  exit 1
fi

trap "exit" INT TERM ERR
trap "kill 0" EXIT

PORT=$RANDOM
kubectl port-forward deployment/main-gateway $PORT:8080 -n $NAMESPACE_NAME &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$PORT -Dheadless=true" clean test
cd ../ || exit
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  kubectl delete namespace $NAMESPACE_NAME
  exit 1
else
  echo "Tests passed successfully"
fi

echo "Deployment finished."