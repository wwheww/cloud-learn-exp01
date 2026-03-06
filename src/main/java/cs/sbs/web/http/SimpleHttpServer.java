package cs.sbs.web.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 智学云平台基础HTTP服务器
 * 监听8080端口，处理HTTP请求
 */
public class SimpleHttpServer {
    
    private final int port;
    private final ExecutorService executorService;
    private volatile boolean running = true;
    
    public SimpleHttpServer(int port) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(10);
    }
    
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 智学云平台服务器启动成功！");
            System.out.println("📍 访问地址: http://localhost:" + port);
            System.out.println("⏹️  按Enter键停止服务器...\n");
            
            // 在单独线程中监听停止命令
            new Thread(() -> {
                try {
                    System.in.read();
                    running = false;
                    shutdown();
                    // 强制退出，因为accept()是阻塞的
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("服务器启动失败: " + e.getMessage());
            }
        }
    }
    
    private void shutdown() {
        System.out.println("\n🛑 服务器正在关闭...");
        executorService.shutdown();
        System.out.println("✅ 服务器已关闭");
    }
    
    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer(8080);
        server.start();
    }
}
