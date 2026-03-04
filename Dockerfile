# Stage 1: Build the WAR using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -q

# Stage 2: Deploy to WildFly
FROM quay.io/wildfly/wildfly:latest
COPY --from=build /app/target/wildfly-mcp-server.war /opt/jboss/wildfly/standalone/deployments/
EXPOSE 8080
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
