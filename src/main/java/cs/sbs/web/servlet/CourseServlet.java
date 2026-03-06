package cs.sbs.web.servlet;

import cs.sbs.web.bean.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/courses")
public class CourseServlet extends HttpServlet {

    private static final List<Course> courseList = new ArrayList<>();

    static {
        courseList.add(new Course(1, "Java基础", 100.0, 40));
        courseList.add(new Course(2, "Web开发", 200.0, 50));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><head><title>课程列表</title></head><body>");
            out.println("<h1>课程列表 (GET)</h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>名称</th><th>价格</th><th>时长</th></tr>");
            for (Course c : courseList) {
                out.println("<tr>");
                out.println("<td>" + c.getId() + "</td>");
                out.println("<td>" + c.getName() + "</td>");
                out.println("<td>" + c.getPrice() + "</td>");
                out.println("<td>" + c.getDuration() + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<br/><a href='index.html'>返回首页</a>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求编码，防止中文乱码
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String name = req.getParameter("name");
        String priceStr = req.getParameter("price");
        String durationStr = req.getParameter("duration");

        double price = 0.0;
        int duration = 0;
        try {
            price = Double.parseDouble(priceStr);
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            // ignore or handle error
        }

        Course newCourse = new Course(courseList.size() + 1, name, price, duration);
        courseList.add(newCourse);

        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><head><title>添加成功</title></head><body>");
            out.println("<h1>添加课程成功 (POST)</h1>");
            out.println("<p>课程名称: " + name + "</p>");
            out.println("<p>价格: " + price + "</p>");
            out.println("<p>时长: " + duration + "</p>");
            out.println("<br/><a href='courses'>查看课程列表</a>");
            out.println("<br/><a href='index.html'>返回首页</a>");
            out.println("</body></html>");
        }
    }
}
