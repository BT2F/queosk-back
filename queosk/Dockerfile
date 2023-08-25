# open jdk 8 버전의 환경을 구성
FROM adoptopenjdk/openjdk8:latest

ARG JAR_FILE=*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
