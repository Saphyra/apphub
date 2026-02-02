THREAD_COUNT=$1
BUILD_MODE=$2

echo "Using $THREAD_COUNT with build mode $BUILD_MODE"

eval "$(minikube docker-env)"
docker rmi -f $(docker images -a -q)

if [ "$BUILD_MODE" == "SKIP_TESTS" ]; then
  echo "Skipping tests"
  mvn -T $THREAD_COUNT clean install -Dmaven.test.skip=true
else
  mvn -T $THREAD_COUNT clean install
fi

BUILD_RESULT=$?
if [[ "$BUILD_RESULT" -ne 0 ]]; then
  echo "Build failed."
  exit 1
fi

cd apphub-frontend
npm run build
docker build -f Dockerfile -t saphyra/apphub-frontend:latest .
cd ..