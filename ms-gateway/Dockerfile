FROM openjdk:17
WORKDIR /app
EXPOSE 8765
COPY ./target/ms-gateway-0.0.1-SNAPSHOT.jar ms-gateway.jar
ENTRYPOINT java -jar ms-gateway.jar
