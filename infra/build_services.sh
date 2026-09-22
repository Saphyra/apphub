BUILD_OPERATION=$1
THREAD_COUNT=$2
SKIP_TESTS=$3
SERVICES=$4

eval "$(minikube docker-env)"
docker rmi -f $(docker images -a -q)

if [ "$SKIP_TESTS" == "true" ]; then
  MVN_COMMAND="mvn -T $THREAD_COUNT clean \"$BUILD_OPERATION\" -pl \"$SERVICES\" -am -DskipTests"
else
  MVN_COMMAND="mvn -T $THREAD_COUNT clean \"$BUILD_OPERATION\" -pl \"$SERVICES\" -am"
fi

echo "Executing: $MVN_COMMAND"
eval "$MVN_COMMAND"

BUILD_RESULT=$?
if [[ "$BUILD_RESULT" -ne 0 ]]; then
  echo "Build failed."
  exit 1
fi