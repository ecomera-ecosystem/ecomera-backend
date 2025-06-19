FROM openjdk:17-jdk-alpine

LABEL maintainer="youssef.ammari.795@gmail.com"

COPY target/ecomera-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]