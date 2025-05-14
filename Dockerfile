FROM amazoncorretto:21-alpine-jdk

COPY target/email-*.jar /email.jar

CMD ["java","-jar","/email.jar"]

