THREAD_COUNT=$1

eval "$(minikube docker-env)"
docker rmi -f $(docker images -a -q)

mvn -T $THREAD_COUNT clean deploy

BUILD_RESULT=$?
if [[ "$BUILD_RESULT" -ne 0 ]]; then
  echo "Build failed."
  exit 1
fi

cd apphub-frontend
npm run build
docker build -f Dockerfile -t saphyra/apphub-frontend:latest .
docker tag saphyra/apphub-frontend:latest saphyra/apphub-frontend:release
cd ..