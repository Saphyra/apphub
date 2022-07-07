SERVICES=$1
OPTIONS=$2

declare -A serviceData

while IFS= read -r line; do
    IFS=' ' read -r -a array <<< "$line"
    service=${array[0]}
    serviceData[$service]=$line
done < ./infra/deployment/service/service_list

#Build specific modules
artifactIds=()
IFS=',' read -r -a services <<< "$SERVICES"
for service in "${services[@]}"
do
    data=${serviceData[$service]}
    IFS=' ' read -r -a splittedData <<< "$data"
    artifactId=${splittedData[1]}
    artifactIds+=(":$artifactId")
done

echo "Artifacts to build: ${artifactIds[*]}"

./clean_up_space.sh
eval "$(minikube docker-env)"

command=$(IFS=, ; echo "${artifactIds[*]}")

if [ "$OPTIONS" == "skipTests" ]; then
  mvn -T 12 clean install -pl "$command" -am -DskipTests
else
  mvn -T 12 clean install -pl "$command" -am
fi
BUILD_RESULT=$?
if [[ "$BUILD_RESULT" -ne 0 ]]; then
  echo "Build failed."
  exit 1
fi

#deploy
NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)

for service in "${services[@]}"
do
  data=${serviceData[$service]}
  IFS=' ' read -r -a splittedData <<< "$data"
  dirname=${splittedData[2]}

  trimmedDirname="$(sed -e 's/[[:space:]]*$//' <<<"${dirname}")"
  file="./infra/deployment/service/develop/$trimmedDirname/$service.yml"

  echo "Deploying $service to namespace $NAMESPACE_NAME with file: $file"

  kubectl -n "$NAMESPACE_NAME" delete deployment "$service"
  kubectl -n "$NAMESPACE_NAME" delete service "$service"
  kubectl apply -n "$NAMESPACE_NAME" -f "$file"
done

./infra/deployment/script/wait_for_pods_ready.sh "$NAMESPACE_NAME" 30 2 5
STARTUP_RESULT=$?
if [[ "$STARTUP_RESULT" -ne 0 ]]; then
  echo "Services failed to start."
  exit 1
fi

start ./port_forward.sh "$NAMESPACE_NAME"