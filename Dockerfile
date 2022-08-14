FROM gradle:jdk18 as build
WORKDIR /app
COPY src/ src/
COPY build.gradle .
COPY MankianersTelegramSpringStarter/ MankianersTelegramSpringStarter/
COPY settings.gradle .
RUN gradle bootJar

FROM openjdk:18-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]