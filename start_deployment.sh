DIRNAME=$PWD
echo "Dirname: $DIRNAME"

mvn -T 16 clean package

kubectl delete deployments --all

kubectl apply -f infra/config.yaml

while IFS= read -r LINE || [[ -n "$LINE" ]]; do
  if [[ "$LINE" =~ [^[:space:]] ]]; then
    echo ""

    TRIMMED="${LINE/$'\r'/}"
    IFS=' ' read -r -a LINE_SPLIT <<<"$TRIMMED"
    LOCATION=${LINE_SPLIT[0]}
    TAG=${LINE_SPLIT[1]}

    echo "Location: $LOCATION"
    echo "Tag: $TAG"

    cd "$LOCATION" || exit

    DOCKER_COMMAND="./build_deploy.sh $TAG"
    echo "Command: $DOCKER_COMMAND"
    $DOCKER_COMMAND &

    cd "$DIRNAME" || exit
  fi
done <infra/services

wait
