NAMESPACE=${1:-develop}

IFS=' ' read -r -a mgwarr <<< "$(netstat -aon | findstr 9001)"
taskkill //PID "$(sed -e 's/^[[:space:]]*//' <<<"${mgwarr[4]}")" //f

IFS=' ' read -r -a psqlarr <<< "$(netstat -aon | findstr 9002)"
taskkill //PID "$(sed -e 's/^[[:space:]]*//' <<<"${psqlarr[4]}")" //f

kubectl port-forward deployment/main-gateway 9001:8080 -n "$NAMESPACE" &
kubectl port-forward deployment/postgres 9002:5432 -n "$NAMESPACE" &