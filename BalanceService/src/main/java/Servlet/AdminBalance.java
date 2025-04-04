/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Servlet;

import BalanceApi.Balance;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.hibernate.*;
import util.HibernateUtil;
import jakarta.json.*;
import java.io.StringReader;

/**
 *
 * @author omar
 */
@WebServlet(name = "AdminBalance", urlPatterns = {"/AdminBalance"})
public class AdminBalance extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                List<Balance> balances = session.createSelectionQuery("from Balance", Balance.class).list();
                session.getTransaction().commit();
                
                StringBuilder jsonBuilder = new StringBuilder("[");
                for (int i = 0; i < balances.size(); i++) {
                    Balance balance = balances.get(i);
                    jsonBuilder.append("{")
                        .append("\"id\":").append(balance.getId()).append(",")
                        .append("\"msisdn\":\"").append(balance.getMsisdn()).append("\",")
                        .append("\"balance\":").append(balance.getValue())
                        .append("}");
                    if (i < balances.size() - 1) {
                        jsonBuilder.append(",");
                    }
                }
                jsonBuilder.append("]");
                out.print(jsonBuilder.toString());
            } catch (Exception e) {
                throw new HibernateException("Error getting balances", e);
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Read the request body
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        // Parse JSON using JSON-P
        JsonReader jsonReader = Json.createReader(new StringReader(buffer.toString()));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        
        Balance balance = new Balance();
        balance.setMsisdn(jsonObject.getString("msisdn"));
        balance.setValue((float) jsonObject.getJsonNumber("balance").doubleValue());

        // Save to database using Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(balance);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new HibernateException("Error saving balance", e);
        }
        
        // Send back success response
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\":true}");
        }
    }

    @Override
    protected void doPut (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Read the request body
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
    
            // Parse JSON using JSON-P
            JsonReader jsonReader = Json.createReader(new StringReader(buffer.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
            
            Balance balance = new Balance();
            balance.setMsisdn(jsonObject.getString("msisdn"));
            balance.setValue((float) jsonObject.getJsonNumber("balance").doubleValue());
    
            // Save to database using Hibernate
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();
                Balance existingBalance = session.createSelectionQuery("from Balance where msisdn = :msisdn", Balance.class)
                                               .setParameter("msisdn", balance.getMsisdn())
                                               .uniqueResult();
                if (existingBalance != null) {
                    existingBalance.setValue(balance.getValue());
                    session.merge(existingBalance);
                }
                session.getTransaction().commit();
            } catch (Exception e) {
                throw new HibernateException("Error saving balance", e);
            }
            
            // Send back success response
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"success\":true}");
            }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Read the request body
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        // Parse JSON using JSON-P
        JsonReader jsonReader = Json.createReader(new StringReader(buffer.toString()));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        
        String msisdn = jsonObject.getString("msisdn");

        // Delete from database using Hibernate
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            
            // First find the existing balance
            Balance balance = session.createSelectionQuery("from Balance where msisdn = :msisdn", Balance.class)
                                   .setParameter("msisdn", msisdn)
                                   .uniqueResult();
            
            if (balance != null) {
                session.remove(balance);
                session.getTransaction().commit();
                // Send back success response
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"success\":true}");
                }
            } else {
                session.getTransaction().rollback();
                // Send back error response - record not found
                try (PrintWriter out = response.getWriter()) {
                    out.print("{\"success\":false,\"error\":\"Record not found\"}");
                }
            }
        } catch (Exception e) {
            // Send error response
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
            }
            throw new HibernateException("Error deleting balance", e);
        }
    }

}
