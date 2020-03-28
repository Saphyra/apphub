TAG=$1
IMAGE_NAME=saphyra/apphub-service-common-web_content
IMAGE="saphyra/$IMAGE_NAME:$TAG"

echo "Image: $IMAGE"

docker build -f Dockerfile -t "$IMAGE" .
docker push "$IMAGE"

kubectl apply -f k8s_deployment.yml
kubectl expose deployment common-web-content --type=NodePort