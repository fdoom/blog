# 빌드 단계
FROM gradle:jdk21 as builder

WORKDIR /app

COPY . .

RUN gradle clean build --no-daemon

# 실행 단계
FROM openjdk:21-slim

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 어플리케이션 실행
CMD ["java", "-jar", "app.jar"]