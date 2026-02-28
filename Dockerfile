# 构建阶段 - 使用 Maven 和 Node.js
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# 复制整个项目
COPY . .

# 列出目录结构进行调试
RUN echo "=== Listing build directory ===" && ls -la

# 检查是否存在 lanjii 目录，如果没有则列出所有文件
RUN echo "=== Checking for pom.xml ===" && \
    find . -name "pom.xml" -type f | head -10

# 尝试构建 - 查找 pom.xml 并在其所在目录构建
RUN mvn clean package -DskipTests -q -f lanjii/pom.xml || \
    mvn clean package -DskipTests -q -f ./pom.xml || \
    (echo "Cannot find pom.xml" && exit 1)

# 运行阶段 - 使用轻量级 Java 镜像
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/lanjii/lanjii-application/target/lanjii-application-*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
