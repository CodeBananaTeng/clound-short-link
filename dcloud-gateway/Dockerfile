FROM adoptopenjdk/openjdk11:jre11u-nightly
COPY target/dcloud-gateway.jar dcloud-account.jar
ENTRYPOINT ["java","-jar","/dcloud-gateway.jar"]