package com.example.controller;

import com.example.util.DatabaseConnection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String action = request.getParameter("action");

    if ("logout".equalsIgnoreCase(action)) {
        handleLogout(request, response);
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("register".equalsIgnoreCase(action)) {
            handleRegister(request, response);
        } else if ("login".equalsIgnoreCase(action)) {
            handleLogin(request, response);
        } else if ("logout".equalsIgnoreCase(action)) {
            handleLogout(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String f_Name = request.getParameter("f_name");
        String l_Name = request.getParameter("l_name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (password == null || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/register.jsp?error=empty_fields");
            return;
        }

        String hashedPassword = hashPasswordSHA256(password);
        String sql = "INSERT INTO users (f_name, l_name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f_Name);
            stmt.setString(2, l_Name);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=registered");
            } else {
                response.sendRedirect(request.getContextPath() + "/register.jsp?error=failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register.jsp?error=db_error");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
                
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (password == null || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=empty_fields");
            return;
        }

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    String inputHashedPassword = hashPasswordSHA256(password);
                    
                    if (inputHashedPassword.equals(storedHashedPassword)) {
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", rs.getInt("id"));
                        session.setAttribute("userEmail", rs.getString("email"));
                        session.setAttribute("userName", rs.getString("f_name") + " " + rs.getString("l_name"));
                        
                Cookie lastLoginCookie = new Cookie("lastLogin", java.time.LocalDateTime.now().toString());
                lastLoginCookie.setMaxAge(7 * 24 * 60 * 60);
                lastLoginCookie.setPath("/");
                response.addCookie(lastLoginCookie);

                        response.sendRedirect(request.getContextPath() + "/main.jsp");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
                    }
                } else {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=db_error");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp?status=logged_out");
    }

    private String hashPasswordSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}