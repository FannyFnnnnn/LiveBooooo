# Railway 部署指南 - Lanjii 項目

## 📋 部署概述

這是一個 **Spring Boot 3 + Vue 3** 的前後端分離項目，將在 Railway 上部署：
- **前端**：Vue3 構建版本（靜態 HTML + CSS + JS）
- **後端**：Spring Boot 應用（JAR 包）
- **數據庫**：MySQL 8.0

---

## ✅ 前置準備

### 1. 確保本地環境正常
```bash
# 檢查 Java 版本（需要 17+）
java -version

# 檢查 Maven 版本
mvn -version

# 檢查 Node 版本（需要 18+）
node -version
```

### 2. 構建應用（本地測試）
```bash
# 在根目錄執行
cd lanjii

# 編譯後端
mvn clean package -DskipTests

# 編譯前端
cd lanjii-admin-ui
npm install
npm run build

# 檢查是否生成成功
# 後端：lanjii-application/target/lanjii-application-*.jar
# 前端：lanjii-admin-ui/dist
```

### 3. 推送到 GitHub
```bash
git add .
git commit -m "準備 Railway 部署"
git push origin main
```

---

## 🚀 Railway 部署步驟

### 第一步：在 Railway 上創建 MySQL 數據庫

1. 登錄 [Railway.app](https://railway.app)
2. 點擊 **"New Project"** → **"Provision New"** → 選擇 **"MySQL"**
3. 等待部署完成（2-3 分鐘）
4. 在 **"MySQL"** 卡片中，查看連接信息：
   - **host**: `localhost` (Railway 內部使用)
   - **port**: `3306`
   - **database**: `railway` (或自定義)
   - **user**: `root`
   - **password**: (會顯示)
5. **複製連接字符串** 或記下上述信息

### 第二步：初始化數據庫

1. 使用任何 MySQL 客戶端連接到 Railway MySQL
   ```bash
   mysql -h <host> -u root -p
   ```

2. 執行初始化 SQL 腳本
   ```bash
   # 使用項目中的 SQL 文件
   mysql -h <host> -u root -p database_name < lanjii/docs/sql/lanjii-v3.sql
   ```

3. 驗證數據庫是否正確初始化
   ```sql
   SHOW TABLES;
   SELECT COUNT(*) FROM sys_user;  -- 檢查是否有數據
   ```

---

### 第三步：部署後端應用

1. 在 Railway Dashboard 中，點擊 **"New"** → **"GitHub Repo"**
2. 連接你的 GitHub 倉庫
3. 選擇部署分支：**main**
4. 配置構建設置：
   - **Build Command**: 
     ```
     cd lanjii && mvn clean package -DskipTests
     ```
   - **Start Command**: 
     ```
     java -jar lanjii-application/target/lanjii-application-3.0.0.jar
     ```
   
   > **注意**：根據實際的 JAR 包名稱調整（可能是 `lanjii-application-*.jar`）

5. 添加環境變量

在後端應用的 **Variables** 中設置：

```
# 數據庫配置
SPRING_DATASOURCE_URL=jdbc:mysql://<mysql-host>:3306/<database-name>?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=<mysql-password>

# Spring 配置
SPRING_PROFILES_ACTIVE=demo
SERVER_PORT=8080

# JWT 秘鑰（生成隨機值）
JWT_SECRET=your-secret-key-here-generate-a-random-string-min-32-chars

# 應用端口
SERVER_SERVLET_CONTEXT_PATH=/api
```

6. 確認 MySQL 網絡連接
   - 點擊 MySQL 服務卡片
   - 在右側找到 **"Public Networking"** 並啟用
   - 複製 **Public URL** 用於上面的 `SPRING_DATASOURCE_URL`

---

### 第四步：部署前端應用

1. 在 Railway 再次點擊 **"New"** → **"GitHub Repo"**
2. 選擇相同的倉庫
3. 配置前端設置：
   - **Build Command**:
     ```
     cd lanjii/lanjii-admin-ui && npm install && npm run build
     ```
   - **Start Command**: 
     ```
     npx serve dist -l 3000
     ```
   - **Install Command**: (留空，npm install 在 Build Command 中)

4. 添加環境變量

```
# 後端 API 地址（使用後端應用的公開 URL）
VITE_API_BASE_URL=https://<your-backend-railway-url>/api
NODE_ENV=production
```

> **獲取後端 URL**：在後端應用的工程詳情中，找到 **"Domains"** 部分

5. 點擊 **Deploy** 開始部署

---

## 🔧 環境變量詳細說明

### 後端應用環境變量

| 變量名 | 說明 | 示例 |
|------|------|------|
| `SPRING_DATASOURCE_URL` | MySQL 連接字符串 | `jdbc:mysql://mysql.railway.internal:3306/railway` |
| `SPRING_DATASOURCE_USERNAME` | 數據庫用戶名 | `root` |
| `SPRING_DATASOURCE_PASSWORD` | 數據庫密碼 | (來自 Railway MySQL) |
| `SPRING_PROFILES_ACTIVE` | 激活配置文件 | `demo` |
| `SERVER_PORT` | 應用端口 | `8080` |
| `JWT_SECRET` | JWT 加密密鑰 | (生成 32+ 字符隨機字符串) |

### 前端應用環境變量

| 變數名 | 說明 | 示例 |
|------|------|------|
| `VITE_API_BASE_URL` | 後端 API 基礎 URL | `https://your-app.railway.app/api` |
| `NODE_ENV` | 環境類型 | `production` |

---

## 📝 部署後檢查清單

部署完成後，按照以下步驟驗證：

- [ ] **後端應用**
  ```bash
  # 訪問應用健康檢查 (如果配置了)
  curl https://<backend-url>/health
  ```

- [ ] **前端應用**
  ```bash
  # 訪問前端首頁
  https://<frontend-url>/admin/login
  ```

- [ ] **登錄測試**
  - 用戶名：`admin`
  - 密碼：`123456`

- [ ] **檢查日誌**
  - 在 Railway Dashboard 中查看應用日誌
  - 確認沒有連接錯誤

---

## 🐛 常見問題排查

### 1. 數據庫連接失敗

**症狀**：`Cannot get a connection` 錯誤

**解決方案**：
```bash
# 1. 檢查 MySQL 是否運行
# 2. 檢查防火墻設置 - 需要啟用 Public Networking
# 3. 檢查連接字符串中的主機名是否正確
# 4. 驗證用戶名和密碼
```

### 2. 前端無法連接後端 API

**症狀**：CORS 錯誤或 404

**解決方案**：
```bash
# 1. 檢查 VITE_API_BASE_URL 是否正確
# 2. 檢查後端是否啟用了 CORS
# 3. 確認後端 /api 路由是否配置
```

### 3. 應用啟動失敗

**症狀**：應用狀態為 red，無法訪問

**解決方案**：
- 在 Railway Dashboard 中查看 Logs
- 檢查所有必需的環境變量是否設置
- 確認 Start Command 是否正確

### 4. 內存或資源不足

**症狀**：應用在運行後自動重啟

**解決方案**：
- 在 Railway 中增加分配的內存/CPU
- 優化應用配置（如數據庫連接池大小）

---

## 💡 最佳實踐

1. **使用私有環境變量**
   - 不要在代碼中提交敏感信息
   - 在 Railway Variables 中設置

2. **監控應用日誌**
   - Railway 提供實時日誌查看
   - 定期檢查錯誤信息

3. **定期備份數據庫**
   - Railway 提供備份功能
   - 在 MySQL 卡片中配置自動備份

4. **設置自動重新部署**
   - Railway 支持 GitHub webhook
   - 推送代碼時自動重新部署

5. **使用自定義域名**
   - Railway 允許連接自定義域名
   - 配置 DNS CNAME 記錄

---

## 📚 相關資源

- [Railway 官方文檔](https://docs.railway.app)
- [Spring Boot 部署指南](https://spring.io/guides/gs/deploying-to-cloud/)
- [Vite 部署指南](https://vitejs.dev/guide/static-deploy.html)
- [MySQL 連接驅動配置](https://dev.mysql.com/doc/connector-j/en/)

---

## 🆘 需要幫助？

如果遇到問題：
1. 檢查 Railway Dashboard 中的應用日誌
2. 驗證所有環境變量是否正確設置
3. 確保數據庫已初始化並可訪問
4. 檢查網絡連接和防火牆設置

祝部署順利！ 🎉
