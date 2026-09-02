# ==========================================================
# BUILD STAGE
# ==========================================================

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

# Tests are executed before deployment in our workflow.
# Here we only build the production JAR.
RUN mvn clean package -DskipTests


# ==========================================================
# RUNTIME STAGE
# ==========================================================

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "app.jar"]
