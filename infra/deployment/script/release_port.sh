PORT=$1

if [[ $PORT != 5432 ]]; then
  echo "Releasing port $PORT"
  IFS=' ' read -r -a mgwarr <<< "$(netstat -aon | findstr $PORT)"
  taskkill //PID "$(sed -e 's/^[[:space:]]*//' <<<"${mgwarr[4]}")" //f
fi