package cs.sbs.web.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求处理器
 * 解析请求并生成响应
 */
public class RequestHandler implements Runnable {
    
    private final Socket clientSocket;
    
    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            OutputStream output = clientSocket.getOutputStream();
            // Force UTF-8 encoding for response
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(output, StandardCharsets.UTF_8), true)
        ) {
            // 读取请求行
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            
            System.out.println("[" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + requestLine);
            
            // 解析请求
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                return; 
            }
            String method = parts[0];
            String path = parts[1];
            
            // 读取请求头
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                int colonIndex = headerLine.indexOf(':');
                if (colonIndex > 0) {
                    headers.put(headerLine.substring(0, colonIndex).trim(),
                               headerLine.substring(colonIndex + 1).trim());
                }
            }
            
            // 路由处理
            String response = routeRequest(method, path);
            writer.println(response);
            
        } catch (IOException e) {
            System.err.println("处理请求时出错: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String routeRequest(String method, String path) {
        if (!"GET".equals(method)) {
            return HttpResponse.methodNotAllowed();
        }
        
        // Remove query parameters for simple routing
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        
        switch (path) {
            case "/":
            case "/home":
                return HttpResponse.ok(buildHomePage());
            case "/courses":
                return HttpResponse.ok(buildCourseListPage());
            case "/about":
                return HttpResponse.ok(buildAboutPage());
            default:
                return HttpResponse.notFound();
        }
    }
    
    private String buildHomePage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>智学云在线学习平台</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    h1 { color: #2c3e50; }
                    .nav { margin: 20px 0; }
                    .nav a { margin-right: 20px; text-decoration: none; color: #3498db; }
                    .welcome { background: #ecf0f1; padding: 20px; border-radius: 5px; }
                </style>
            </head>
            <body>
                <h1>📚 智学云在线学习平台</h1>
                <div class="nav">
                    <a href="/">首页</a>
                    <a href="/courses">课程列表</a>
                    <a href="/about">关于我们</a>
                </div>
                <div class="welcome">
                    <h2>欢迎光临！</h2>
                    <p>智学云提供优质的在线课程，助您随时随地提升技能。</p>
                    <p>🎯 今日推荐：《Java核心技术》《Spring Boot实战》</p>
                </div>
            </body>
            </html>
            """;
    }
    
    private String buildCourseListPage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>课程列表 - 智学云</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    h1 { color: #2c3e50; }
                    .course { border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .course h3 { margin-top: 0; color: #3498db; }
                    .price { color: #e74c3c; font-weight: bold; }
                </style>
            </head>
            <body>
                <h1>📖 热门课程</h1>
                <div class="course">
                    <h3>Java核心技术（完整版）</h3>
                    <p>讲师：张老师 | 时长：48小时</p>
                    <p class="price">¥199.00</p>
                </div>
                <div class="course">
                    <h3>Spring Boot实战开发</h3>
                    <p>讲师：李老师 | 时长：36小时</p>
                    <p class="price">¥299.00</p>
                </div>
                <div class="course">
                    <h3>前端开发从入门到精通</h3>
                    <p>讲师：王老师 | 时长：60小时</p>
                    <p class="price">¥249.00</p>
                </div>
                <p><a href="/">← 返回首页</a></p>
            </body>
            </html>
            """;
    }
    
    private String buildAboutPage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>关于我们 - 智学云</title>
            </head>
            <body style="font-family: Arial, sans-serif; margin: 40px;">
                <h1>关于智学云</h1>
                <p>智学云是一家专注于IT技术教育的在线学习平台。</p>
                <p>项目版本：v1.0.0</p>
                <p>技术支持：Spring Framework</p>
                <p><a href="/">← 返回首页</a></p>
            </body>
            </html>
            """;
    }
}
