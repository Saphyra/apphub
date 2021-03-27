./build.sh "$1"

NAMESPACE_NAME=$(git rev-parse --abbrev-ref HEAD)
echo "Namespace: $NAMESPACE_NAME"

./deploy.sh "$NAMESPACE_NAME"

if [ "$1" != "skipTests" ]; then
    ./run_tests.sh "$NAMESPACE_NAME"
fi

start ./port_forward.sh "$NAMESPACE_NAME"