FROM openjdk:17
ADD target/application.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MinRAMPercentage=75.0", "-XX:MaxRAMPercentage=75.0", "-XX:+CrashOnOutOfMemoryError", "-jar", "application.jar"]