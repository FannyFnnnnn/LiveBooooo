# 构建阶段 - 使用 Maven 和 Node.js
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# 复制整个项目
COPY . .

# 定位 pom.xml 所在目录
RUN if [ -f lanjii/pom.xml ]; then \
    cd lanjii && \
    mvn clean package -DskipTests -q && \
    cd .. ; \
else \
    mvn clean package -DskipTests -q ; \
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
