package com.example.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            default:
                request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}