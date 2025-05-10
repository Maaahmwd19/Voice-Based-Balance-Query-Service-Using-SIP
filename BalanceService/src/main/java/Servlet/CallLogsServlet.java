package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/callLogs")
public class CallLogsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Call Logs</title>");
        out.println("<link rel='stylesheet' href='styles/admin.css'>");
        out.println("</head><body>");
        out.println("<h1>Call Logs</h1>");
        out.println("<table border='1'><tr><th>Caller</th><th>Callee</th><th>Start Time</th><th>End Time</th><th>Duration (sec)</th></tr>");

        try (
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "19012001");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM call_logs ORDER BY start_time DESC");
        ) {
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("caller") + "</td>");
                out.println("<td>" + rs.getString("callee") + "</td>");
                out.println("<td>" + rs.getTimestamp("start_time") + "</td>");
                out.println("<td>" + rs.getTimestamp("end_time") + "</td>");
                out.println("<td>" + rs.getInt("duration") + "</td>");
                out.println("</tr>");
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='5'>Error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace();
        }

        out.println("</table>");
        out.println("<a href='admin.html' class='admin-button'>Back to Admin</a>");
        out.println("</body></html>");
    }
}
