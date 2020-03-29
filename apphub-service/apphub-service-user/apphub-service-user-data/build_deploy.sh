TAG=$1
IMAGE_NAME=apphub-service-user_data
IMAGE="saphyra/$IMAGE_NAME:$TAG"

echo "Image: $IMAGE"

docker build -f Dockerfile -t "$IMAGE" .
docker push "$IMAGE"

kubectl apply -f k8s_deployment.yml
kubectl delete service user-data
kubectl expose deployment user-data --type=ClusterIP
