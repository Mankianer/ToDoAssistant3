FROM gradle:jdk18 as build
WORKDIR /app
COPY src/ src/
COPY build.gradle .
COPY settings.gradle .
RUN gradle bootJar
RUN ls -al build/libs

FROM openjdk:18-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
RUN ls -al
CMD ["java", "-jar", "app.jar"]