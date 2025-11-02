FROM openjdk:21-jdk-slim

COPY app/boot/build/libs/boot-1.jar app.jar

CMD ["java", "-jar","app.jar"]

