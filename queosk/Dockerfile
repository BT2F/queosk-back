# open jdk 8 버전의 환경을 구성
FROM adoptopenjdk/openjdk8:latest

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

COPY ${JAR_PATH}/queosk-0.0.1-SNAPSHOT.jar ${JAR_PATH}/queosk-0.0.1-SNAPSHOT.jar

CMD ["java","-jar","./build/libs/queosk-0.0.1-SNAPSHOT.jar"]
