function deploy() {
  while IFS= read -r LINE || [[ -n "$LINE" ]]; do
    if [[ "$LINE" =~ [^[:space:]] ]]; then
      TRIMMED="${LINE/$'\r'/}"
      IFS=' ' read -r -a LINE_SPLIT <<<"$TRIMMED"

      if [[ "${LINE_SPLIT[0]}" == "$1" ]]; then
        echo ""

        kubectl delete deployment "$1"

        LOCATION=${LINE_SPLIT[1]}
        TAG=${LINE_SPLIT[2]}

        ./deploy.sh "$LOCATION" "$TAG" &
      fi
    fi
  done <infra/services
}

DIRNAME=$PWD
echo "Dirname: $DIRNAME"
echo "Deploying services $1"

./build.sh

IFS=',' read -r -a APP_NAMES <<<"$1"
for APP_NAME in "${APP_NAMES[@]}"; do
  deploy "$APP_NAME"
done

wait
