# 在线秒杀系统

基于 Spring Boot 3 + Vue 3 的限时秒杀演示系统：管理员创建秒杀活动，普通用户浏览商品、参与秒杀下单，并在订单中完成支付、取消或确认收货。核心实现**数据库条件扣减防超卖、同一用户限购一件、活动时间窗口校验、订单状态机流转**。

## 一、技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.2.5、Spring Security、MyBatis-Plus 3.5.5、MySQL 8、JWT（jjwt 0.11.5）、BCrypt |
| 前端 | Vue 3.5、Vite 5、Pinia 2、Vue Router 4、Axios 1 |
| 数据库 | MySQL 8.x，字符集 utf8mb4 |

## 二、目录结构

```
在线秒杀系统/
├── 需求文档.md                  ← 需求规格（用户故事、功能/非功能需求、数据模型、UML 清单）
├── README.md                    ← 本文件
├── database/
│   └── init.sql                 ← 建表语句 + 种子数据（管理员/用户/商品/活动）
├── backend/                     ← Spring Boot 后端工程
│   ├── pom.xml
│   └── src/main/java/com/seckill/
│       ├── common/              ← 统一返回 Result、错误码、异常、分页、角色常量
│       ├── config/              ← Security、CORS、MyBatis-Plus 配置
│       ├── security/            ← JWT 工具、过滤器、登录主体、上下文工具
│       ├── controller/          ← 认证、商品、活动、订单接口
│       ├── service/ + impl/    ← 业务服务与实现
│       ├── mapper/              ← 数据访问层
│       ├── domain/              ← entity / dto / vo
│       └── engine/              ← 订单状态机
└── frontend/                    ← Vue 3 + Vite 前端工程
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                 ← 接口封装（auth/product/order/admin）
        ├── router/               ← 路由与登录/角色守卫
        ├── store/               ← Pinia 用户状态
        ├── utils/               ← 请求封装、枚举、格式化
        ├── components/          ← 头部导航、商品卡片、状态标签、空状态
        └── views/               ← 登录/注册、大厅、详情、订单、活动管理、404
```

## 三、环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+ 与 npm

## 四、启动步骤

### 4.1 初始化数据库

登录 MySQL 后执行脚本（脚本会自动创建 `seckill_db` 库）：

```bash
SOURCE C:/Users/朱国昱/Desktop/软件工程与UML/在线秒杀系统/database/init.sql;
```

数据库连接配置位于 `backend/src/main/resources/application.yml`，默认 `root/root`、`localhost:3306`，请按本机实际情况修改。

### 4.2 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认端口 `8080`，接口统一前缀 `/api`。

### 4.3 启动前端

```bash
cd frontend
npm install
npm run dev
```

浏览器访问终端提示的地址（默认 `http://localhost:5173`）。Vite 已配置 `/api` 代理到 `http://localhost:8080`。

## 五、演示账号

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 普通用户 USER | `buyer` | `123456` |
| 管理员 ADMIN | `admin` | `123456` |

密码在数据库中以 BCrypt 加密存储，种子数据以 `init.sql` 为准。

## 六、接口概览

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/login` | 登录 | 公开 |
| GET | `/api/auth/me` | 当前用户 | 登录 |
| POST | `/api/auth/logout` | 退出 | 登录 |
| GET | `/api/products` | 商品分页列表（关键字搜索） | 公开 |
| GET | `/api/products/{id}` | 商品详情 | 公开 |
| POST | `/api/orders` | 秒杀下单 | 登录 |
| GET | `/api/orders` | 我的订单列表 | 登录 |
| GET | `/api/orders/{id}` | 订单详情 | 登录 |
| POST | `/api/orders/{id}/cancel` | 取消订单（库存回补） | 登录 |
| POST | `/api/orders/{id}/pay` | 模拟支付 | 登录 |
| POST | `/api/orders/{id}/complete` | 确认收货 | 登录 |
| GET | `/api/admin/activities` | 活动列表 | 管理员 |
| POST | `/api/admin/activities` | 新建活动 | 管理员 |
| PUT | `/api/admin/activities` | 编辑活动 | 管理员 |

统一响应结构：`{ "code": 0, "message": "success", "data": ..., "timestamp": ... }`，`code = 0` 表示成功。

## 七、订单状态与流转

| 状态 | 编码 | 可流转到 |
| --- | --- | --- |
| 待支付 | `PENDING_PAYMENT` | 已支付、已取消 |
| 已支付 | `PAID` | 已完成 |
| 已完成 | `COMPLETED` | 终态 |
| 已取消 | `CANCELLED` | 终态 |

状态流转规则集中在后端 `engine/OrderStateMachine.java`，非法跳转直接返回业务异常。

## 八、部署说明（简述）

1. **后端**：`mvn clean package` 生成 `backend/target/*.jar`，上传服务器后 `java -jar seckill-backend-1.0.0.jar`；通过环境变量或外部 `application.yml` 覆盖数据库连接、JWT 密钥等配置。
2. **前端**：`npm run build` 生成 `frontend/dist/` 静态资源，交由 Nginx 托管，并将 `/api` 反向代理到后端服务。
3. **数据库**：在生产库执行 `database/init.sql` 建表与种子数据。
