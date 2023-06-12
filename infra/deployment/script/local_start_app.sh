echo "Local starting service $2"
start java -Dfile.encoding=UTF-8 -Dspring.profiles.active=local -jar "$2"