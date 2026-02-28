# 构建阶段 - 使用 Maven 和 Node.js
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# 从 GitHub 直接克隆（包括 submodule）
RUN apt-get update && apt-get install -y git && \
    git clone --depth 1 --recurse-submodules --remote-submodules https://github.com/FannyFnnnnn/LiveBooooo.git temp_repo && \
    cd temp_repo && \
    git submodule status

# 构建项目
WORKDIR /build/temp_repo/lanjii
RUN mvn clean package -DskipTests

# 运行阶段 - 使用轻量级 Java 镜像
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/temp_repo/lanjii/lanjii-application/target/lanjii-application-*.jar app.jar

# 复制启动脚本
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# 暴露端口
EXPOSE 8080

# 环境变量默认值
ENV DB_HOST=localhost \
    DB_PORT=3306 \
    DB_USERNAME=root \
    DB_PASSWORD=root

# 启动应用
ENTRYPOINT ["/app/docker-entrypoint.sh"]
