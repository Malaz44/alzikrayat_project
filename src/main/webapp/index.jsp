<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Alzikrayat - Share Your Memories</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">

    <nav class="navbar navbar-light bg-white shadow-sm px-4">
        <span class="navbar-brand fw-bold">Alzikrayat</span>
        <div>
            <% if (session.getAttribute("userName") != null) { %>
                <a href="main.jsp" class="btn btn-outline-primary btn-sm">Hi <%= session.getAttribute("userName") %></a>
                <a href="auth?action=logout" class="btn btn-outline-danger btn-sm">Logout</a>
            <% } else { %>
                <a href="login.jsp" class="btn btn-primary btn-sm">Please Login</a>
            <% } %>
        </div>
    </nav>

    <div class="container text-center py-5">
        <h1 class="display-4">Welcome to Alzikrayat</h1>
        <p class="lead">A place to share your photos and memories with everyone.</p>
        <a href="<%= request.getContextPath() %>/photo" class="btn btn-success btn-lg mt-3">Browse Photo Gallery</a>
    </div>

    <div class="container py-5">
        <h2>About Us</h2>
        <p>
            Alzikrayat  is a dedicated photo-sharing and personal diary web application help users to capture and cherish ther special moments.
             It allows users to create personal accounts, safely upload photos accompainied by meaningful descriptions and interact through comments on shared memories.
        </p>
    </div>

</body>
</html>