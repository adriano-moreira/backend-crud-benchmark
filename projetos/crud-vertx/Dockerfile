FROM eclipse-temurin:21-jre-alpine

ADD target/*-SNAPSHOT-fat.jar app.jar

ENTRYPOINT [ "java", "-jar","app.jar" ]
