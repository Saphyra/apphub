set -e

NAMESPACE_NAME=${2:-$(git rev-parse --abbrev-ref HEAD)}
echo "Namespace: $NAMESPACE_NAME"

./infra/deployment/script/build.sh "$1"
./infra/deployment/script/build_frontend.sh

./infra/deployment/script/deploy.sh "$NAMESPACE_NAME"

start ./port_forward.sh "$NAMESPACE_NAME"
if [ "$1" != "skipTests" ]; then
  if [ "$1" != "skipIntegrationTests" ]; then
    ./run_tests.sh "$NAMESPACE_NAME"
  fi
fi