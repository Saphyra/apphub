set -e

./infra/deployment/script/build.sh "$1"

if [ "$1" != "skipDeploy" ]; then
  if [ "$1" != "skipBuild" ]; then
    ./infra/deployment/script/build_frontend.sh
  fi

  NAMESPACE_NAME=${2:-$(git rev-parse --abbrev-ref HEAD)}
  echo "Namespace: $NAMESPACE_NAME"
  ./infra/deployment/script/deploy.sh "$NAMESPACE_NAME"
  start ./port_forward.sh "$NAMESPACE_NAME"
fi