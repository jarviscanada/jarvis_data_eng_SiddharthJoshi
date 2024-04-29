# Build Stage
FROM maven:3.9.6-amazoncorretto-17 AS build
COPY src /build/src
COPY pom.xml /build
RUN mvn -f /build/pom.xml clean package -DskipTests

# Packaging Stage
FROM amazoncorretto:17-alpine3.19
COPY --from=build /build/target/trading-app-1.0-SNAPSHOT.jar /usr/local/app/trading/lib/trading_app.jar
ENTRYPOINT ["java", "-jar", "/usr/local/app/trading/lib/trading_app.jar"]
