git checkout master
git pull

./develop_deployment.sh

eval "$(minikube docker-env)"

echo "Logging in to docker with username $1..."
docker login -u "$1" -p "$2"

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
./deploy.sh "$NAMESPACE_NAME" "$NAMESPACE_NAME"


SERVER_PORT=$RANDOM
DATABASE_PORT=$RANDOM
kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n $NAMESPACE_NAME &
kubectl port-forward deployment/postgres $DATABASE_PORT:5432 -n "$NAMESPACE_NAME" &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=true" clean test
cd ../ || exit
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
  kubectl delete namespace $NAMESPACE_NAME
  exit 1
else
  echo "Tests passed successfully"
fi

./release_port.sh $SERVER_PORT
./release_port.sh $DATABASE_PORT

echo "Deployment finished."

taskkill //F //IM chromedriver.exe //T

start ./pp.sh