echo "Local starting service $2"
start java -Xmx512m -Dfile.encoding=UTF-8 -DSPRING_ACTIVE_PROFILE=local -jar "$2"

echo "Pinging $1 as $2"
curl -s -o nul --head -X GET --silent --retry 20 --retry-connrefused --retry-delay 1 localhost:$1/platform/health
echo "Ping $1 finished. $2 started successfully."