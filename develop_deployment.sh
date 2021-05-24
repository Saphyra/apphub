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
./run_tests.sh "$NAMESPACE_NAME"
echo "Deployment finished."

start ./port_forward.sh $NAMESPACE_NAME