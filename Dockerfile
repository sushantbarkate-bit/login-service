FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY target/*.jar login-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "login-service.jar"]
