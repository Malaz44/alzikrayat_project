package com.example.controller;

import com.example.model.Memory;
import com.example.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/memory")
public class MemoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("delete".equalsIgnoreCase(action)) {
            String memoryIdParam = request.getParameter("id");
            if (memoryIdParam != null) {
                deleteMemory(Integer.parseInt(memoryIdParam), userId);
            }
            response.sendRedirect(request.getContextPath() + "/main.jsp");
            return;
        }

        List memories = getMemoriesByUserId(userId);
        request.setAttribute("memories", memories);
        request.getRequestDispatcher("/views/photos/main.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title != null && !title.trim().isEmpty() && content != null && !content.trim().isEmpty()) {
            saveMemory(userId, title, content);
        }

        response.sendRedirect(request.getContextPath() + "/views/photos/main.jsp");
    }

    private void saveMemory(int userId, String title, String content) {
        String sql = "INSERT INTO memories (user_id, title, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List getMemoriesByUserId(int userId) {
        List list = new ArrayList<>();
        String sql = "SELECT * FROM memories WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Memory m = new Memory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                    );
                    list.add(m);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void deleteMemory(int memoryId, int userId) {
        String sql = "DELETE FROM memories WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memoryId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}