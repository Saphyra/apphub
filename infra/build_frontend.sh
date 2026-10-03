TAG=$1

eval "$(minikube docker-env)"

cd apphub-frontend
npm run build
docker build -f Dockerfile -t saphyra/apphub-frontend:$TAG .
cd ..