#!/bin/bash

# base 이미지 설정
FROM openjdk:8-jre-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
