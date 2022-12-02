PORT=$1

IFS=' ' read -r -a mgwarr <<< "$(netstat -aon | findstr $PORT)"
taskkill //PID "$(sed -e 's/^[[:space:]]*//' <<<"${mgwarr[4]}")" //f