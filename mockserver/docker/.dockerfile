FROM ubuntu:latest

EXPOSE 8431
ENV PATH="$PATH:/opt/gradle/gradle-8.8/bin"

RUN apt-get update && apt-get -y -qq install unzip
RUN curl --location --show-error -O --url https://services.gradle.org/distributions/gradle-8.8-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-8.8-bin.zip

COPY ../ /development/
WORKDIR /development

RUN gradle wrapper
RUN ./gradlew clean
RUN ./gradlew build

ENTRYPOINT ["/bin/sh", "-c", "./gradlew run"]