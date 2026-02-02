THREAD_COUNT=$1
BUILD_MODE=$2
SERVICES=$3

eval "$(minikube docker-env)"
docker rmi -f $(docker images -a -q)

if [ "$BUILD_MODE" == "SKIP_TESTS" ]; then
  mvn -T $THREAD_COUNT clean install -pl "$SERVICES" -am -DskipTests
else
  mvn -T $THREAD_COUNT clean install -pl "$SERVICES" -am
fi

BUILD_RESULT=$?
if [[ "$BUILD_RESULT" -ne 0 ]]; then
  echo "Build failed."
  exit 1
fi