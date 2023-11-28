FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar
RUN apk --no-cache add curl
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]