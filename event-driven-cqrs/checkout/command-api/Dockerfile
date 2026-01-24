FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/checkout-command-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
