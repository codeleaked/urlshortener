FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9001:8080 9002:8080
CMD ["java","-Dspring.data.mongodb.uri=mongodb://mongodbhost:27017/url", "-jar","/app.jar"]
