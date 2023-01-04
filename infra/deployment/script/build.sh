if [ "$1" != "skipBuild" ]; then
  ./infra/deployment/script/clean_up_space.sh
  eval "$(minikube docker-env)"

  if [ "$1" == "skipTests" ]; then
    mvn -T 24 clean install -DskipTests -fae
  elif [ "$1" == "skipUnitTests" ]; then
    mvn -T 24 clean install -DskipTests -fae
  else
    mvn -T 6 clean install -fae
  fi
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi