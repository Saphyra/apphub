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