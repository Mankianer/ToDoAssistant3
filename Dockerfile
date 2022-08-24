FROM gradle:jdk18 as build
WORKDIR /app
COPY src/ src/
COPY build.gradle .
COPY MankianersTelegramSpringStarter/ MankianersTelegramSpringStarter/
COPY settings.gradle .
RUN gradle bootJar

FROM eclipse-temurin:18-jdk-alpine

ARG app_name='ToDoAssistant'
ENV app_name=${app_name}

WORKDIR /app
COPY --from=build /app/build/libs/*.jar ${app_name}.jar
EXPOSE 8080

ENV springProfilesActive=''

ENTRYPOINT java -XX:+UseContainerSupport -Dspring.profiles.active=${springProfilesActive} -Dtelegram.user.file=/app/data/telegram/users -jar /app/${app_name}.jar