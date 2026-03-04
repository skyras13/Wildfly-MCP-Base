# Use the official WildFly image from Quay.io
FROM quay.io/wildfly/wildfly:latest

# Copy the built WAR file into the WildFly deployments directory
COPY target/wildfly-mcp-server.war /opt/jboss/wildfly/standalone/deployments/

# Expose the default HTTP port
EXPOSE 8080

# Command to run WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
