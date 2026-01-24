FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/checkout-saga-handler.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
