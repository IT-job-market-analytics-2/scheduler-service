FROM maven:3.9.4 AS build
WORKDIR /scheduler-service
COPY pom.xml .
RUN mvn verify
COPY . .
RUN ["mvn", "package", "-Dmaven.test.skip=true"]

FROM openjdk:23
WORKDIR /scheduler-service
COPY --from=build /scheduler-service/target/*.jar scheduler-service.jar
ENTRYPOINT ["java", "-jar", "scheduler-service.jar"]