# 构建阶段 - 使用 Maven 和 Node.js  
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# 复制整个项目
COPY . .

# 初始化 git（如果 lanjii 是 submodule）
RUN apt-get update && apt-get install -y git && \
    git config --global user.email "build@railway.app" && \
    git config --global user.name "Railway Build"

# 初始化 submodule（如果存在 .gitmodules）
RUN if [ -f .gitmodules ]; then \
    git init && \
    git submodule update --init --recursive ; \
    fi || true

# 列出目录进行调试
RUN echo "=== Build directory contents ===" && ls -la && \
    echo "=== Looking for pom.xml ===" && find . -name "pom.xml" -type f

# 构建 - 使用 -f 参数指定 pom.xml 位置
RUN if [ -f lanjii/pom.xml ]; then \
    mvn clean package -DskipTests -q -f lanjii/pom.xml ; \
elif [ -f pom.xml ]; then \
    mvn clean package -DskipTests -q ; \
else \
    echo "ERROR: Cannot find pom.xml" && exit 1 ; \
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
