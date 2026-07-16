FROM eclipse-temurin:22-jdk

ADD target/SpringEcom.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]