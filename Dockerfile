#!/bin/bash

# base 이미지 설정
FROM openjdk:8-jre-alpine

# jar 파일 위치를 변수로 설정
ARG JAR_FILE=./build/libs/*.jar

# 환경변수 설정
ENV CUSTOM_NAME default

# jar 파일을 컨테이너 내부에 복사
COPY ${JAR_FILE} test-app.jar

# 외부 호스트 8080 포트로 노출
EXPOSE 8080

# 실행 명령어
CMD ["java", "-Dtest.customName=${CUSTOM_NAME}", "-jar", "test-app.jar"]
