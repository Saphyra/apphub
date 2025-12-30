FROM eclipse-temurin:25-jdk
ADD target/application.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60", "-XX:InitialRAMPercentage=60", "-jar", "application.jar"]