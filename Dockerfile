FROM maven:3.9.7-amazoncorretto-21 AS build

COPY pom.xml ./
COPY .mvn .mvn

COPY src src

RUN mvn clean install -DskipTests

FROM amazoncorretto:21

WORKDIR bookstore

COPY --from=build target/*.jar urlshorteningservice.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "urlshorteningservice.jar"]