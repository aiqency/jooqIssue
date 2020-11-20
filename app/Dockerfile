FROM openjdk:11-jdk
LABEL maintainer "support@croit.io"
LABEL name "croit.datajet.manager"

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

ARG JAR_FILE

COPY build/libs/$JAR_FILE /datajet/app.jar

ENTRYPOINT ["java", "-jar", "/datajet/app.jar"]
