git checkout master
git pull

./develop_deployment.sh

eval "$(minikube docker-env)"

echo "Logging in to docker with username $1..."
docker login -u "$1" -p "$2"

while IFS="" read -r image_name || [ -n "$image_name" ]
do
  TRIMMED="$(sed -e 's/[[:space:]]*$//' <<<"${image_name}")"
  echo "$image_name"
  docker tag saphyra/"$TRIMMED":latest saphyra/"$TRIMMED":release
  docker push saphyra/"$TRIMMED":release &
done < ./infra/deployment/service/service_list

wait

#Deploying to production
NAMESPACE_NAME="production"
./deploy.sh "$NAMESPACE_NAME" "$NAMESPACE_NAME"
./run_production_tests.sh

echo "Deployment finished."

taskkill //F //IM chromedriver.exe //T

start ./pp.sh