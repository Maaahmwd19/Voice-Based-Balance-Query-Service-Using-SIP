package com.balance;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/call-logs")
public class CallLogsServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres"; 
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "19012001"; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Call Logs</title></head><body>");
        out.println("<h2>Call Logs</h2>");
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>Caller</th><th>Callee</th><th>Start Time</th><th>End Time</th><th>Duration (s)</th></tr>");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM call_logs ORDER BY start_time DESC")) {

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("caller") + "</td>");
                out.println("<td>" + rs.getString("callee") + "</td>");
                out.println("<td>" + formatTimestamp(rs.getTimestamp("start_time")) + "</td>");
                out.println("<td>" + formatTimestamp(rs.getTimestamp("end_time")) + "</td>");
                out.println("<td>" + rs.getInt("duration") + "</td>");
                out.println("</tr>");
            }

        } catch (SQLException e) {
            out.println("<tr><td colspan='6'>Database error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace();
        }

        out.println("</table>");
        out.println("</body></html>");
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
