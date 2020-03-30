TAG=$1
IMAGE_NAME=apphub-service-user_authentication
IMAGE="saphyra/$IMAGE_NAME:$TAG"

echo "Image: $IMAGE"

docker build -f Dockerfile -t "$IMAGE" .
docker push "$IMAGE"

kubectl apply -f k8s_deployment.yml
kubectl delete service user-authentication
kubectl expose deployment user-authentication --type=ClusterIP
