FROM openjdk:17-jdk-slim-buster
WORKDIR /app
EXPOSE 8080
COPY /build/libs/neoris-0.0.1-SNAPSHOT.jar build/

WORKDIR /app/build
ENTRYPOINT java -jar neoris-0.0.1-SNAPSHOT.jar
