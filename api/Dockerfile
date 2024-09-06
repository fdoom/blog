FROM gradle:jdk21

WORKDIR /app

COPY . .

RUN gradle clean build --no-daemon

CMD ["java", "-jar", "build/libs/api-0.0.1-SNAPSHOT.jar"]