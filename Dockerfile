FROM openjdk:17-jdk-slim
WORKDIR /server-app
COPY target/digicore-be.war .
CMD java -jar ./digicore-be.war
