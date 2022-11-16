git checkout master
git pull

./build_and_deploy.sh "" develop

eval "$(minikube docker-env)"

echo "Logging in to docker with username $1..."
docker login -u "$1" -p "$2"

while IFS="" read -r service_data || [ -n "$service_data" ]
do
  IFS=' ' read -r -a array <<< "$service_data"
  image_name=${array[1]}
  TRIMMED="$(sed -e 's/[[:space:]]*$//' <<<"${image_name}")"
  echo "$TRIMMED"
  docker tag saphyra/"$TRIMMED":latest saphyra/"$TRIMMED":release
  docker push saphyra/"$TRIMMED":release &
done < ./infra/deployment/service/service_list

wait

#Deploying to production
NAMESPACE_NAME="production"
./infra/deployment/script/deploy.sh "$NAMESPACE_NAME" "$NAMESPACE_NAME" 180
./run_production_tests.sh

echo "Deployment finished."

start ./pp.sh