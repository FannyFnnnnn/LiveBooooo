# 构建阶段 - 使用 Maven 和 Node.js
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制整个项目
COPY . .

# 构建后端
RUN cd lanjii && mvn clean package -DskipTests -q

# 构建前端
RUN cd lanjii/lanjii-admin-ui && \
    apt-get update && apt-get install -y node-npm && \
    npm install && \
    npm run build

# 运行阶段 - 使用轻量级 Java 镜像
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/lanjii/lanjii-application/target/lanjii-application-*.jar app.jar

# 从构建阶段复制前端构建结果
COPY --from=builder /app/lanjii/lanjii-admin-ui/dist /app/static

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
