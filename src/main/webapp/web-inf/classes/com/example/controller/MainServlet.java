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

@WebServlet("/")
public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/login":
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                break;

            case "/register":
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                break;

            case "/main":
            default:
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("userId") != null) {
                    loadMemoriesAndForward(request, response, session);
                } else {
                    response.sendRedirect(request.getContextPath() + "/login");
                }
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("addMemory".equals(action)) {
            addMemory(request, response, session);
        } else {
            response.sendRedirect(request.getContextPath() + "/main");
        }
    }

    private void addMemory(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {

        int userId = (int) session.getAttribute("userId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

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

        response.sendRedirect(request.getContextPath() + "/main");
    }

    private void loadMemoriesAndForward(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        int userId = (int) session.getAttribute("userId");
        List<Memory> memories = new ArrayList<>();
        String sql = "SELECT id, user_id, title, content, created_at FROM memories WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    memories.add(new Memory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("memories", memories);
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}