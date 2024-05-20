THREAD_COUNT=$1
BUILD_MODE=$2

eval "$(minikube docker-env)"
docker rmi -f $(docker images -a -q)

if [ "$1" == "SKIP_TESTS" ]; then
  mvn -T $THREAD_COUNT clean install -DskipTests
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