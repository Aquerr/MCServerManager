FROM centos

RUN yum install -y java-11-openjdk-devel

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]