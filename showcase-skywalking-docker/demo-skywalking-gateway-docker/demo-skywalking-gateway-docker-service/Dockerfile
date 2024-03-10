FROM openjdk:8
EXPOSE 7001
MAINTAINER jdktomcater jdktomcater@gmail.com
ADD target/demo-skywalking-gateway-docker-service*.jar /spring-boot.jar
ENTRYPOINT ["java", "-jar","/spring-boot.jar"]