
ARG TARGETARCH
FROM --platform=linux/amd64 gradle:jdk18 as build-amd64
RUN echo "I'm building for $TARGETARCH"
FROM --platform=linux/amd64 eclipse-temurin:18-jdk-alpine as stage-amd64

ARG TARGETARCH
FROM --platform=linux/arm64 gradle:jdk18-jammy as build-arm64
RUN echo "I'm building for $TARGETARCH"
FROM --platform=linux/arm64 eclipse-temurin:18-jdk-jammy as stage-arm64

ARG TARGETARCH
FROM build-${TARGETARCH} as build

WORKDIR /app
COPY src/ src/
COPY build.gradle .
COPY MankianersTelegramSpringStarter/ MankianersTelegramSpringStarter/
COPY settings.gradle .
RUN gradle bootJar

ARG TARGETARCH
FROM stage-${TARGETARCH} as final

ARG app_name='ToDoAssistant'
ENV app_name=${app_name}

WORKDIR /app
COPY --from=build /app/build/libs/*.jar ${app_name}.jar
EXPOSE 8080

ENV springProfilesActive=''

ENTRYPOINT java -XX:+UseContainerSupport -Dspring.profiles.active=${springProfilesActive} -Dtelegram.user.file=/app/data/telegram/users -jar /app/${app_name}.jar