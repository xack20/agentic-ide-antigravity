FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/checkout-command-handler.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
