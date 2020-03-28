mvn -T 16 clean package

DIRNAME=$(dirname "$0")
echo  "Dirname: $DIRNAME"

while IFS= read -r line || [[ -n "$line" ]]; do
    echo  "$line"

    IFS=' ' read -r -a LINE_SPLIT <<< "$line"
    LOCATION=${LINE_SPLIT[0]}
    TAG=${LINE_SPLIT[1]}

    IFS='/' read -r -a LOCATION_SPLIT <<< "$LOCATION"
    echo "Location: $LOCATION"
    echo "Tag: $TAG"

    cd "$LOCATION" || exit

    DOCKER_COMMAND="./build_deploy.sh $TAG"
    echo  "Command: $DOCKER_COMMAND"
    $DOCKER_COMMAND &

    cd "$DIRNAME" || exit
done < infra/services

wait