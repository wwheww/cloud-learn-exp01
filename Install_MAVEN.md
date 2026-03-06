在 Windows 上安装 Maven 3.8+ 的核心步骤包括：**安装JDK、下载解压、配置环境变量、验证**四步。

### 一、前置条件：安装并配置 JDK
Maven 3.8+ 要求 **JDK 1.8 及以上**。
1. 安装 JDK（如 JDK17、21、25）
2. 配置 `JAVA_HOME` 环境变量（指向 JDK 根目录）
3. 验证：打开新 CMD，执行
   ```bash
   java -version
   javac -version
   ```
   正常输出版本号即可。

---

### 二、下载 Maven 3.8+
1. 访问官网下载页：https://maven.apache.org/download.cgi
2. 找到 **3.8.x / 3.9.x** 系列，下载 **Binary zip archive**（如 `apache-maven-3.8.8-bin.zip`）
3. 解压到**无中文、无空格**的目录（推荐：`D:\Develop\apache-maven-3.8.8`）

---

### 三、配置环境变量（关键）
1. 打开环境变量：
    - 右键「此电脑」→「属性」→「高级系统设置」→「环境变量」
2. 新建系统变量：
    - 变量名：`MAVEN_HOME`
    - 变量值：你的 Maven 解压目录（如 `D:\Develop\apache-maven-3.8.8`）
3. 编辑 `Path` 变量：
    - 找到系统变量里的 `Path` → 编辑 → 新建
    - 添加：`%MAVEN_HOME%\bin`
4. 一路「确定」保存所有窗口。

---

### 四、验证安装
**必须新开一个 CMD / PowerShell 窗口**，执行：
```bash
mvn -v
```
成功输出示例：
```
Apache Maven 3.8.8 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: D:\Develop\apache-maven-3.8.8
Java version: 1.8.0_391, vendor: Oracle Corporation
Java home: C:\Program Files\Java\jdk1.8.0_391\jre
Default locale: zh_CN, platform encoding: GBK
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

---

### 五、可选：配置本地仓库与镜像（推荐）
1. 进入 `conf` 目录，打开 `settings.xml`
2. 配置本地仓库路径（默认在 `C:\Users\用户名\.m2\repository`）：
   ```xml
   <localRepository>D:\Develop\maven-repo</localRepository>
   ```
3. 配置阿里云镜像（加速下载）：
   ```xml
   <mirrors>
     <mirror>
       <id>aliyunmaven</id>
       <mirrorOf>central</mirrorOf>
       <url>https://maven.aliyun.com/repository/public</url>
     </mirror>
   </mirrors>
   ```
