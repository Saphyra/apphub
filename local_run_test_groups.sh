ENABLED_GROUPS=$1
DISABLED_GROUPS=${2}
HEADLESS=${3:-true}
SERVER_PORT=${4:-8080}
DATABASE_PORT=${5:-5432}
INTEGRATION_SERVER_PORT=${6:-8072}
THREAD_COUNT=${7:-20}

./infra/deployment/script/start_integration_server.sh $INTEGRATION_SERVER_PORT

function waitStartup(){
  echo "Pinging $1"
  curl -s -o nul --head -X GET --silent --retry 20 --retry-connrefused --retry-delay 1 localhost:$1/platform/health
}

while IFS="" read -r service_data || [ -n "$service_data" ]
do
  IFS=' ' read -r -a array <<< "$service_data"
  port=${array[3]}
  TRIMMED="$(sed -e 's/[[:space:]]*$//' <<<"${port}")"

  if [[ $port != "null" ]]; then
    waitStartup $TRIMMED
  fi
done < ./infra/deployment/service/service_list

cd apphub-integration || exit
mvn -DthreadCount="$THREAD_COUNT" -DargLine="-DthreadCount=$THREAD_COUNT -DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=$HEADLESS -DretryEnabled=true -DrestLoggingEnabled=false -DdisabledGroups=$DISABLED_GROUPS -DenabledGroups=$ENABLED_GROUPS -DdatabaseName=apphub -DintegrationServerEnabled=true" clean test
cd .. || exit

taskkill //F //IM chromedriver.exe //T