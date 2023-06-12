./local_stop.sh

if [ "$1" != "skipBuild" ]; then
  if [ "$1" == "skipTests" ]; then
    mvn -T 24 clean package -DskipTests -fae
  else
    mvn -T 6 clean package -fae
  fi
  BUILD_RESULT=$?
  if [[ "$BUILD_RESULT" -ne 0 ]]; then
    echo "Build failed."
    exit 1
  fi
fi

function waitStartup(){
  echo "Pinging $1"
  curl -s -o nul --head -X GET --silent --retry 20 --retry-connrefused --retry-delay 1 localhost:$1/platform/health
}

./infra/deployment/script/local_start_app.sh 8081 "./apphub-service/apphub-service-platform/apphub-service-platform-event_gateway/target/application.jar"
waitStartup 8081

./infra/deployment/script/local_start_app.sh 8080 "./apphub-service/apphub-service-platform/apphub-service-platform-main_gateway/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8083 "./apphub-service/apphub-service-platform/apphub-service-platform-web_content/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8099 "./apphub-service/apphub-service-platform/apphub-service-platform-storage/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8082 "./apphub-service/apphub-service-platform/apphub-service-platform-scheduler/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8094 "./apphub-service/apphub-service-platform/apphub-service-platform-message_sender/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8098 "./apphub-service/apphub-service-platform/apphub-service-platform-encryption/target/application.jar" &

./infra/deployment/script/local_start_app.sh 8091 "./apphub-service/apphub-service-skyxplore/apphub-service-skyxplore-data/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8095 "./apphub-service/apphub-service-skyxplore/apphub-service-skyxplore-game/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8093 "./apphub-service/apphub-service-skyxplore/apphub-service-skyxplore-lobby/target/application.jar" &

./infra/deployment/script/local_start_app.sh 8089 "./apphub-service/apphub-service-admin_panel/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8100 "./apphub-service/apphub-service-utils/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8085 "./apphub-service/apphub-service-user/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8086 "./apphub-service/apphub-service-training/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8090 "./apphub-service/apphub-service-notebook/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8088 "./apphub-service/apphub-service-modules/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8096 "./apphub-service/apphub-service-community/target/application.jar" &
./infra/deployment/script/local_start_app.sh 8097 "./apphub-service/apphub-service-calendar/target/application.jar" &

sleep 20

while IFS="" read -r service_data || [ -n "$service_data" ]
do
  IFS=' ' read -r -a array <<< "$service_data"
  port=${array[3]}
  TRIMMED="$(sed -e 's/[[:space:]]*$//' <<<"${port}")"

  if [[ $port != "null" ]]; then
    waitStartup $TRIMMED
  fi
done < ./infra/deployment/service/service_list