FROM eclipse-temurin:18-jdk

ARG app_name='ToDoAssistant'
ENV app_name=${app_name}

WORKDIR /app
COPY ./build/libs/*.jar ${app_name}.jar
EXPOSE 8080

ENV springProfilesActive=''

ENTRYPOINT java -XX:+UseContainerSupport -Dspring.profiles.active=${springProfilesActive} -Dtelegram.user.file=/app/data/telegram/users -jar /app/${app_name}.jar