FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon bootJar

FROM openjdk:17-jdk-slim
EXPOSE 8080
COPY --from=build /build/libs/task-share-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]