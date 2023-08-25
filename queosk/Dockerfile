#!/bin/bash

# base 이미지 설정
FROM openjdk:8-jre-alpine

COPY ./build/libs/queosk-0.0.1-SNAPSHOT-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
