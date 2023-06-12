HEADLESS=${1:-true}
DISABLED_GROUPS=${2:-}
SERVER_PORT=${3:-8080}
DATABASE_PORT=${4:-5432}

cd apphub-integration || exit
mvn -DargLine="-DserverPort=$SERVER_PORT -DdatabasePort=$DATABASE_PORT -Dheadless=$HEADLESS -DretryEnabled=true -DrestLoggingEnabled=false -DdisabledGroups=$DISABLED_GROUPS -DdatabaseName=apphub" clean test
cd .. || exit

taskkill //F //IM chromedriver.exe //T