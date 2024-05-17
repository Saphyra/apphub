export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8

cd apphub-ci

mvn clean package -DskipTests

cd ..

java -Dfile.encoding=UTF-8 -jar apphub-ci/target/ci.jar