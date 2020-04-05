TAG=$1
IMAGE_NAME=apphub-service-platform-event_gateway
IMAGE="saphyra/$IMAGE_NAME:$TAG"

echo "Image: $IMAGE"

docker build -f Dockerfile -t "$IMAGE" .
docker push "$IMAGE"

kubectl apply -f k8s_deployment.yml
kubectl delete service event-gateway
kubectl expose deployment event-gateway --type=ClusterIP
