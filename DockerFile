# ============================
# Build stage
# ============================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy toàn bộ source
COPY . .

# Build project, tạo file jar
RUN mvn -B clean package -DskipTests

# ============================
# Run stage
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Render dùng PORT env
ENV PORT=8080
EXPOSE 8080

# Chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
