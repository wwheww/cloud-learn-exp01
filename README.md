# CloudLearnX 实践01：项目启动与 Web 基础

- 项目中文名：智学云在线学习平台
- 英文名：CloudLearnX-01
- 代码包名：cs.sbs.web
- 课程定位：Web 开发系列第一讲，聚焦 HTTP 基础与 Servlet 规范，为构建Web服务建立基础

## 学习目标
- 理解 HTTP 请求/响应的基本结构与工作原理
- 掌握使用原生 Java Socket 编写最小可用 HTTP 服务器
- 理解 Servlet 生命周期与路由映射
- 搭建基础 Web 服务，完成首页与课程列表

## 环境准备
- 安装 JDK 17（或更高）并配置 JAVA_HOME
- [安装 Maven（3.8+）](./Install_MAVEN.md)，命令行可执行 `mvn -v`；或使用IDE直接运行
- IDE（IntelliJ IDEA / VS Code）可选

提示：
- 若命令行提示 “mvn: command not found”，请安装 Maven 或使用 IDE 面板运行

## 获取项目
- 项目根目录：cloud-learn-exp01
- 主要配置文件：[pom.xml](./pom.xml)

## 项目结构
```
cloud-learn-exp01/
├── src/main/java/cs/sbs/web/
│   ├── http/                      # 原生 HTTP 服务器
│   │   ├── SimpleHttpServer.java
│   │   ├── RequestHandler.java
│   │   └── HttpResponse.java
│   ├── servlet/                   # Servlet Web 应用
│   │   └── CourseServlet.java     # 课程管理 Servlet (GET/POST)
│   └── bean/                      # 数据实体
│       └── Course.java
├── src/main/webapp/
│   ├── WEB-INF/
│   │   └── web.xml
│   └── index.html                 # 静态入口与表单
└── pom.xml
```

---

## 阶段一：原生 HTTP 服务器

本阶段我们将不依赖任何 Web 容器（如 Tomcat/Jetty），仅使用 Java 标准库中的 `java.net.ServerSocket` 和 `java.net.Socket` 来实现一个能处理 HTTP 请求的服务器。

### 步骤 1：创建项目结构
首先，请在 `src/main/java/cs/sbs/web/http/` 目录下创建三个 Java 文件：
1.  **SimpleHttpServer.java**：作为主程序入口。
2.  **RequestHandler.java**：用于处理客户端请求逻辑。
3.  **HttpResponse.java**：用于封装和生成 HTTP 响应。

### 步骤 2：实现 HTTP 响应工具类 (HttpResponse.java)
HTTP 响应必须严格遵守协议格式（状态行、响应头、空行、响应体）。请编写一个工具类，包含以下功能：
*   **私有构建方法**：接收状态码、状态文本、Content-Type 和响应体内容。使用 `StringBuilder` 拼接字符串：
    *   第一行拼接状态行（如 `HTTP/1.1 200 OK`）。
    *   第二行拼接 `Content-Type` 头。
    *   第三行拼接 `Content-Length` 头（注意使用 UTF-8 字节长度）。
    *   第四行插入一个空行（`\r\n`）。
    *   最后追加响应体内容。
*   **公开静态方法 ok()**：接收 HTML 内容，调用构建方法生成 200 OK 的响应字符串。
*   **公开静态方法 notFound()**：调用构建方法生成 404 Not Found 的响应字符串。

### 步骤 3：实现请求处理器 (RequestHandler.java)
该类需要实现 `Runnable` 接口，以便在多线程环境下运行。
*   **构造函数**：接收一个 `Socket` 对象。
*   **run() 方法逻辑**：
    1.  从 Socket 获取输入流 (`InputStream`)，包装为 `BufferedReader` 以便按行读取。
    2.  从 Socket 获取输出流 (`OutputStream`)，包装为 `PrintWriter` 以便发送文本响应。
    3.  读取第一行请求行（如 `GET /home HTTP/1.1`），并解析出请求路径（如 `/home`）。
    4.  根据路径进行简单路由判断：
        *   如果路径是 `/home` 或 `/`，返回欢迎页面的 HTML（调用 `HttpResponse.ok`）。
        *   如果路径是 `/courses`，返回课程列表的 HTML。
        *   其他路径返回 404 错误（调用 `HttpResponse.notFound`）。
    5.  将生成的响应字符串写入输出流，并在 `finally` 块中关闭 Socket 连接。

### 步骤 4：实现服务器启动类 (SimpleHttpServer.java)
这是程序的入口，负责监听端口并分发请求。
*   **main() 方法逻辑**：
    1.  定义端口号（如 8080）。
    2.  创建一个 `ServerSocket` 实例并绑定到该端口。
    3.  打印启动日志，提示用户服务器已就绪。
    4.  进入一个无限循环 (`while(true)`)：
        *   调用 `serverSocket.accept()` 阻塞等待客户端连接。
        *   一旦接收到连接（返回 `Socket` 对象），就创建一个新的 `Thread`，将 `RequestHandler` 实例传入。
        *   启动线程 (`start()`)，让处理器在后台运行，主线程继续等待下一个连接。

### 步骤 5：运行与验证
1.  **编译并运行**：
    在终端中执行以下命令（确保你在 `cloud-learn-exp01` 目录下）：
    ```bash
    mvn compile exec:java -Dexec.mainClass="cs.sbs.web.http.SimpleHttpServer"
    ```
2.  **测试访问**：
    打开浏览器访问以下地址，观察页面内容：
    *   [http://localhost:8080/home](http://localhost:8080/home) -> 应显示“欢迎来到智学云”
    *   [http://localhost:8080/courses](http://localhost:8080/courses) -> 应显示课程列表
    *   [http://localhost:8080/error](http://localhost:8080/error) -> 应显示 404 错误
3.  **停止服务器**：
    在终端按 `Ctrl + C` 停止运行。

### 延伸思考：
- 为什么要显式设置 UTF-8 编码？
- 如何支持 POST 方法和请求体解析？
- 如何抽象出 Router 与 Controller？


---

## 阶段二：Servlet 与平台首页

在阶段一中，我们手写了 HTTP 解析与响应，非常繁琐且容易出错。阶段二我们将引入 **Web 容器**（本项目使用 Jetty）和 **Servlet 规范**。容器会帮我们处理底层的 Socket 通信、多线程管理，我们只需要专注于编写业务逻辑。

### 步骤 1：定义数据模型 (Course.java)
首先，我们需要一个 Java 类来表示“课程”这个实体，以便在内存中存储和操作数据。
请在 `src/main/java/cs/sbs/web/bean/` 目录下创建 `Course.java` 类：
*   **属性**：定义私有字段，包括课程ID（int）、名称（String）、价格（double）、时长（int）。
*   **方法**：提供无参构造器、全参构造器，以及所有属性的 Getter 和 Setter 方法。

### 步骤 2：编写核心 Servlet (CourseServlet.java)
Servlet 是运行在服务器端的 Java 小程序，用于处理客户端请求。
请在 `src/main/java/cs/sbs/web/servlet/` 目录下创建 `CourseServlet.java`，并继承 `HttpServlet` 类：
1.  **添加注解**：使用 `@WebServlet` 注解将该类映射到 `/courses` 路径，这样访问该路径的请求就会由这个类处理。
2.  **模拟数据库**：在类中定义一个静态列表 (`static List<Course>`)，并在静态代码块 (`static {}`) 中初始化几条测试数据（如“Java基础”、“Web开发”）。
3.  **处理 GET 请求 (doGet)**：
    *   重写 `doGet` 方法。
    *   设置响应类型为 `text/html;charset=UTF-8`。
    *   使用 `PrintWriter` 输出 HTML 代码：
        *   生成一个表格 `<table>`。
        *   遍历静态列表中的课程数据，将每个课程的属性填充到表格行 `<tr>` 中。
        *   添加一个返回首页的链接。
4.  **处理 POST 请求 (doPost)**：
    *   重写 `doPost` 方法。
    *   首先设置请求编码 (`req.setCharacterEncoding("UTF-8")`)，防止中文乱码。
    *   从请求中获取表单参数 (`req.getParameter`)：名称、价格、时长。
    *   将获取到的参数封装成一个新的 `Course` 对象，并添加到静态列表中。
    *   输出一个简单的 HTML 页面，提示“添加成功”，并提供“返回列表”和“返回首页”的链接。

### 步骤 3：编写静态入口页 (index.html)
为了让用户能方便地发起请求，我们需要一个简单的 HTML 页面作为入口。
请修改 `src/main/webapp/index.html` 文件：
*   **查看列表链接**：添加一个超链接 `<a>`，指向 `courses`（即对应 CourseServlet 的 GET 方法）。
*   **添加课程表单**：创建一个 `<form>` 表单。
    *   `action` 属性设置为 `courses`。
    *   `method` 属性设置为 `post`（对应 CourseServlet 的 POST 方法）。
    *   添加三个输入框 `<input>`，分别对应课程名称（text）、价格（number）、时长（number）。
    *   添加一个提交按钮。

### 步骤 4：运行与验证
1.  **启动容器**：
    在终端中运行以下 Maven 命令，启动 Jetty 服务器（端口已配置为 8081）：
    ```bash
    mvn jetty:run
    ```
2.  **功能测试**：
    *   浏览器访问 [http://localhost:8081/](http://localhost:8081/)。
    *   点击“查看课程列表”，确认能看到初始化的课程数据。
    *   点击“返回首页”，在表单中输入新的课程信息（例如：Python, 99.0, 20）并提交。
    *   提交后应显示成功页面。再次查看列表，确认新课程已出现在表格中。

### 建议思考：
- 与原生 HTTP 服务器相比，Servlet 容器提供了哪些能力（线程管理、生命周期、规范化 API）？
- 如何把 `/enroll?id=xx` 做成可处理的 Servlet？
- 如何将课程数据改为从数据库或 JSON 文件读取？

---

## 阶段三：综合检验
- 对比两种实现：
  - 原生：手写协议、灵活但复杂
  - Servlet：规范化 API、容器管理、可扩展
- 总结今日知识点：HTTP、Socket、Servlet、路由、响应构造

---

## 拓展作业（课后）
- 增加搜索功能：`/search?keyword=java`（可在原生与 Servlet 版本中分别实现）
- 课程详情页：`/course?id=1`，展示课程完整信息
- 课程分类浏览：`/courses?category=java`，过滤展示

---

## 常见问题排查
- “mvn: command not found”
  - 安装 Maven；或在 IDE 中使用 Maven 工具窗口运行 `jetty:run`
- 访问报错或端口占用
  - 检查 8080 (阶段一) 或 8081 (阶段二) 端口是否被占用，必要时修改配置
- 中文乱码
  - 确认响应头包含 `Content-Type: text/html; charset=UTF-8`
  - 代码中使用 UTF-8 编码的 Reader/Writer

---

## 参考资料
- Jakarta Servlet 规范：https://jakarta.ee/specifications/servlet/
- Jetty Maven 插件文档：https://jetty.org/docs/jetty/11/programming-guide/maven-jetty/jetty-maven-plugin.html
- HTTP 报文结构（RFC 2616/7230）

---

## 结束语
- 本讲通过“原生 + Servlet”双路径，帮助你从协议到规范逐步掌握 Web 服务器开发的基础。下一讲将引入 Spring 框架的 IoC 与 MVC，进一步提升工程化能力。
