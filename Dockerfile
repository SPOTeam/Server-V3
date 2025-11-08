FROM eclipse-temurin:21-jdk-jammy

COPY app/boot/build/libs/boot-1.jar app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=prod","app.jar"]

