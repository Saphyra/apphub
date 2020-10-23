if [ "$1" != "skipBuild" ]; then
  eval "$(minikube docker-env)"
  ./clean_up_space.sh
  mvn -T 4 clean install
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi

#Deploying to develop
NAMESPACE_NAME="develop"
./deploy.sh "$NAMESPACE_NAME"

./infra/deployment/script/wait_for_pods_ready.sh $NAMESPACE_NAME 60 2
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  exit 1
fi

SERVER_PORT=$RANDOM
DATABASE_PORT=$RANDOM
kubectl port-forward deployment/main-gateway $SERVER_PORT:8080 -n $NAMESPACE_NAME &
kubectl port-forward deployment/postgres $DATABASE_PORT:5432 -n "$NAMESPACE_NAME" &

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=true" clean test
TEST_RESULT=$?
if [[ "$TEST_RESULT" -ne 0 ]]; then
  echo "Tests failed"
else
  echo "Tests passed successfully"
fi
cd ../ || exit

./release_port.sh $SERVER_PORT
./release_port.sh $DATABASE_PORT
echo "Deployment finished."

taskkill //F //IM chromedriver.exe //T

./port_forward.sh $NAMESPACE_NAME