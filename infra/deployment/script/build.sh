if [ "$1" != "skipBuild" ]; then
  ./local_stop.sh
  ./infra/deployment/script/clean_up_space.sh
  eval "$(minikube docker-env)"

  if [ "$1" == "skipTests" ]; then
    mvn -T 24 clean install -DskipTests
  else
    mvn -T 6 clean install
  fi
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi