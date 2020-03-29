DIRNAME=$PWD
echo "Dirname: $DIRNAME"

APP_NAME=$1
echo "Deploying app by name $APP_NAME"

./build.sh

while IFS= read -r LINE || [[ -n "$LINE" ]]; do
  if [[ "$LINE" =~ [^[:space:]] ]]; then
    TRIMMED="${LINE/$'\r'/}"
    IFS=' ' read -r -a LINE_SPLIT <<<"$TRIMMED"

    if [[ "${LINE_SPLIT[0]}" == "$APP_NAME" ]]; then
      echo ""

      kubectl delete deployment "$APP_NAME"

      LOCATION=${LINE_SPLIT[1]}
      TAG=${LINE_SPLIT[2]}

      ./deploy.sh "$LOCATION" "$TAG"
    fi
  fi
done <infra/services