echo "Local starting service $2"
start java -Xmx512m -Dfile.encoding=UTF-8 -Dspring.profiles.active=local -jar "$2"