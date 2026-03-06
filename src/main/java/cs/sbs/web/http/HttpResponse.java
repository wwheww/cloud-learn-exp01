package cs.sbs.web.http;

import java.nio.charset.StandardCharsets;

/**
 * HTTP响应构造工具类
 */
public class HttpResponse {
    
    public static String ok(String body) {
        return buildResponse(200, "OK", "text/html; charset=UTF-8", body);
    }
    
    public static String notFound() {
        String body = """
            <!DOCTYPE html>
            <html>
            <head><title>404 - 页面未找到</title></head>
            <body>
                <h1>404 - 页面未找到</h1>
                <p>您访问的页面不存在。</p>
                <a href="/">返回首页</a>
            </body>
            </html>
            """;
        return buildResponse(404, "Not Found", "text/html; charset=UTF-8", body);
    }
    
    public static String methodNotAllowed() {
        String body = """
            <!DOCTYPE html>
            <html>
            <head><title>405 - 方法不允许</title></head>
            <body>
                <h1>405 - 方法不允许</h1>
                <p>仅支持GET请求。</p>
            </body>
            </html>
            """;
        return buildResponse(405, "Method Not Allowed", "text/html; charset=UTF-8", body);
    }
    
    private static String buildResponse(int statusCode, String statusText, 
                                       String contentType, String body) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        response.append("Content-Type: ").append(contentType).append("\r\n");
        response.append("Content-Length: ").append(body.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
        response.append("Connection: close\r\n");
        response.append("\r\n");
        response.append(body);
        return response.toString();
    }
}
