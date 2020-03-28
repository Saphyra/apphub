TAG=$1
IMAGE_NAME=apphub-service-main_gateway
IMAGE="saphyra/$IMAGE_NAME:$TAG"

echo "Image: $IMAGE"

docker build -f Dockerfile -t "$IMAGE" .
docker push "$IMAGE"

kubectl apply -f k8s_deployment.yml
kubectl delete service main-gateway
kubectl expose deployment main-gateway --type=NodePort