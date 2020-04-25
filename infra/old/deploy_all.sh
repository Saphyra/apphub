while IFS= read -r LINE || [[ -n "$LINE" ]]; do
  if [[ "$LINE" =~ [^[:space:]] ]]; then
    echo ""

    TRIMMED="${LINE/$'\r'/}"
    IFS=' ' read -r -a LINE_SPLIT <<<"$TRIMMED"

    LOCATION=${LINE_SPLIT[1]}
    TAG=${LINE_SPLIT[2]}

    ./deploy.sh "$LOCATION" "$TAG" &
  fi
done <infra/services

wait
