FROM openjdk:17.0-jdk-slim-bullseye AS builder
WORKDIR /app

COPY target/dicom-viewer-backend-0.0.1-SNAPSHOT.jar app.jar

FROM openjdk:17.0-jdk-slim-bullseye

WORKDIR /app

COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
