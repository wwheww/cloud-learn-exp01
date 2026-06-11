package cs.sbs.web.http;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static String build(int statusCode, String statusText, String contentType, String body) {
        StringBuilder sb = new StringBuilder();
        // 1. 状态行
        sb.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        // 2. 响应头 - Content-Type
        sb.append("Content-Type: ").append(contentType).append("; charset=UTF-8\r\n");
        // 3. 响应头 - Content-Length (使用 UTF-8 字节长度)
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        sb.append("Content-Length: ").append(bodyBytes.length).append("\r\n");
        // 4. 空行，分隔头部和体
        sb.append("\r\n");
        // 5. 响应体
        sb.append(body);
        return sb.toString();
    }

    // 200 OK 响应
    public static String ok(String htmlBody) {
        return build(200, "OK", "text/html", htmlBody);
    }

    // 404 Not Found 响应
    public static String notFound() {
        String body = "<!DOCTYPE html><html><head><title>404 Not Found</title></head>" +
                      "<body><h1>404 Not Found</h1><p>你要找的页面不存在。</p></body></html>";
        return build(404, "Not Found", "text/html", body);
    }
}
