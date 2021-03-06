if [ "$1" != "skipBuild" ]; then
  ./clean_up_space.sh
  eval "$(minikube docker-env)"

  if [ "$1" == "skipTests" ]; then
    mvn -T 16 clean install -DskipTests
  else
    mvn -T 4 clean install
  fi
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi

#Deploying to develop
NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)
echo "Namespace: $NAMESPACE_NAME"

./deploy.sh "$NAMESPACE_NAME"

./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 60 2
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  exit 1
fi

if [ "$1" != "skipTests" ]; then
    ./run_tests.sh "$NAMESPACE_NAME"
fi

start ./port_forward.sh "$NAMESPACE_NAME"