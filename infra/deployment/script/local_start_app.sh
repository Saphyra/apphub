echo "Local starting service $2"
start java -Xmx512m -Dfile.encoding=UTF-8 -DSPRING_ACTIVE_PROFILE=local -jar "$2"