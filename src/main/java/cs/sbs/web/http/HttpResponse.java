package cs.sbs.web.http;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;

    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            // 从 Socket 获取输入输出流
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // 1. 读取请求行 (e.g., "GET /home HTTP/1.1")
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("收到请求: " + requestLine);
            String[] parts = requestLine.split(" ");
            String path = parts.length > 1 ? parts[1] : "/";

            // 2. 简单的路由与响应生成
            String response;
            if ("/".equals(path) || "/home".equals(path)) {
                String body = """
                    <!DOCTYPE html>
                    <html>
                    <head><title>智学云 - 首页</title></head>
                    <body>
                        <h1>欢迎来到智学云在线学习平台</h1>
                        <p><a href="/courses">查看课程列表</a></p>
                    </body>
                    </html>
                """;
                response = HttpResponse.ok(body);
            } else if ("/courses".equals(path)) {
                String body = """
                    <!DOCTYPE html>
                    <html>
                    <head><title>智学云 - 课程列表</title></head>
                    <body>
                        <h1>课程列表（原生HTTP）</h1>
                        <ul>
                            <li>Java 基础</li>
                            <li>Web 开发</li>
                            <li>Spring Boot 实战</li>
                        </ul>
                        <p><a href="/home">返回首页</a></p>
                    </body>
                    </html>
                """;
                response = HttpResponse.ok(body);
            } else {
                response = HttpResponse.notFound();
            }

            // 3. 将响应写入输出流
            out.println(response);

        } catch (IOException e) {
            System.err.println("处理请求时发生错误: " + e.getMessage());
        } finally {
            // 4. 确保连接最终被关闭
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("关闭Socket时出错: " + e.getMessage());
            }
        }
    }
}
