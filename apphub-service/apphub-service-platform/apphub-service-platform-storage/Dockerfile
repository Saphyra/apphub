FROM eclipse-temurin:25-jdk
ADD target/application.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-XX:InitialRAMPercentage=70", "-jar", "application.jar"]