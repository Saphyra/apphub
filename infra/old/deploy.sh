LOCATION=$1
TAG=$2

echo "Location: $LOCATION"
echo "Tag: $TAG"

cd "$LOCATION" || exit

DOCKER_COMMAND="./build_deploy.sh $TAG"
echo "Command: $DOCKER_COMMAND"
$DOCKER_COMMAND

cd "$DIRNAME" || exit
