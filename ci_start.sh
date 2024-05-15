cd apphub-ci

mvn clean package -DskipTests

cd ..

java -jar apphub-ci/target/ci.jar