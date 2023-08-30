FROM openjdk:17-oracle
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} flatflex.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "flatflex.jar"]