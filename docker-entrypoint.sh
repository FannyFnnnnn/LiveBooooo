#!/bin/sh
set -e

# 使用環境變數或默認值
DB_HOST="${DB_HOST:=localhost}"
DB_PORT="${DB_PORT:=3306}"
DB_USERNAME="${DB_USERNAME:=root}"
DB_PASSWORD="${DB_PASSWORD:=root}"

# 啟動應用
exec java \
  -Dspring.datasource.url="jdbc:mysql://${DB_HOST}:${DB_PORT}/lanjii_v3?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true" \
  -Dspring.datasource.username="${DB_USERNAME}" \
  -Dspring.datasource.password="${DB_PASSWORD}" \
  -jar app.jar
