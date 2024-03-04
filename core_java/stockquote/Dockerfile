FROM amazoncorretto:17-alpine3.19
COPY target/stockquote-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/app/grep/lib/stockquote.jar
ENTRYPOINT ["java", "-jar", "/usr/local/app/grep/lib/stockquote.jar"]
