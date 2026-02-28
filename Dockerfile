# 构建阶段 - 使用 Maven 和 Node.js
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# 从 GitHub 直接克隆（包括 submodule）
RUN apt-get update && apt-get install -y git && \
    git clone --depth 1 --recurse-submodules --remote-submodules https://github.com/FannyFnnnnn/LiveBooooo.git temp_repo && \
    cp -r temp_repo/lanjii . && \
    rm -rf temp_repo

# 列出目录进行调试
RUN echo "=== Build directory contents ===" && ls -la && \
    echo "=== Checking lanjii contents ===" && ls -la lanjii/ && \
    echo "=== Looking for pom.xml ===" && find . -name "pom.xml" -type f | head -5

# 构建后端
RUN if [ -f lanjii/pom.xml ]; then \
    mvn clean package -DskipTests -q -f lanjii/pom.xml ; \
else \
    echo "ERROR: Cannot find lanjii/pom.xml" && \
    find . -type f -name "*.xml" && \
    exit 1 ; \
fi

# 运行阶段 - 使用轻量级 Java 镜像
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/lanjii/lanjii-application/target/lanjii-application-*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
