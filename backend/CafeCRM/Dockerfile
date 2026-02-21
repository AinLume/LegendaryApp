FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /app/logs && \
    addgroup --system spring && \
    adduser --system --ingroup spring spring && \
    chown -R spring:spring /app && \
    chmod -R 755 /app

ENV JAVA_OPTS: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Dlogging.level.com.example.tasks_service=DEBUG -Dlogging.level.liquibase=INFO -Dlogging.level.org.springframework=INFO"
ENV TZ=Europe/Moscow
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

USER spring:spring
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]