TAG=$1
IMAGE_NAME=apphub-service-common-web_content
IMAGE="saphyra/$IMAGE_NAME:$TAG"

echo "Image: $IMAGE"

docker build -f Dockerfile -t "$IMAGE" .
docker push "$IMAGE"

kubectl apply -f k8s_deployment.yml
kubectl delete service common-web-content
kubectl expose deployment common-web-content --type=NodePort