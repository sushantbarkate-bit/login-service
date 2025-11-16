# ---------- Stage 1: Build the application ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cache optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the jar
COPY . .
RUN mvn clean package -DskipTests


# ---------- Stage 2: Run the application ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the jar built from stage 1
COPY --from=build /app/target/login-service-0.0.1-SNAPSHOT.jar login-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "login-service.jar"]
