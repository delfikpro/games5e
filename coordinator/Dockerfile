FROM openjdk:8-jre
ADD build/libs/coordinator.jar app.jar
ADD configurations.json configurations.json
ENV CONFIGURATIONS_PATH=configurations.json
ENV GAMES5E_NAMESPACE=games5e
ENTRYPOINT ["java","-jar","/app.jar","--port","1123"]