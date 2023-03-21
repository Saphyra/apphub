cd apphub-frontend

eval "$(minikube docker-env)"

npm run build
BUILD_RESULT=$?
if [[ "$BUILD_RESULT" -ne 0 ]]; then
  echo "Build failed."
  exit 1
fi

docker build -f Dockerfile -t saphyra/apphub-frontend:latest .

cd ..