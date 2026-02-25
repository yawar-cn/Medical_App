FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system medapp && adduser --system --ingroup medapp medapp
COPY --from=builder /app/target/medapp-1.0.0.jar app.jar
RUN chown -R medapp:medapp /app
USER medapp
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
