<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.model.Memory" %>
<%
    String userName = (String) session.getAttribute("userName");
    if (userName == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Memory> memories = (List<Memory>) request.getAttribute("memories");
    if (memories == null) {
        memories = new java.util.ArrayList<>();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Alzikrayat Home Page</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light container mt-5">

    <div class="card p-4 shadow-sm mb-4">
        <div class="d-flex justify-content-between align-items-center">
            <h2>Welcome to Alzikrayat, <%= userName %>!</h2>
            <a href="<%= request.getContextPath() %>/photo" class="btn btn-primary btn-sm">Photo Gallery</a>
            <a href="auth?action=logout" class="btn btn-outline-danger btn-sm">Logout</a>
        </div>
        <hr>

        <h4 class="mb-3">Add New Memory</h4>
        <form action="main" method="post" class="mb-4">
            <input type="hidden" name="action" value="addMemory">
            <div class="mb-2">
                <input type="text" name="title" class="form-control" placeholder="Memory Title" required>
            </div>
            <div class="mb-2">
                <textarea name="content" class="form-control" placeholder="Write your memory here..." rows="3" required></textarea>
            </div>
            <button type="submit" class="btn btn-success">Save Memory</button>
        </form>

        <hr>

        <h4 class="mb-3">My Memories</h4>
        <% if (memories.isEmpty()) { %>
            <p class="text-muted">No memories added yet!</p>
        <% } else { %>
            <% for (Memory m : memories) { %>
                <div class="border rounded p-2 mb-2">
                    <strong><%= m.getTitle() %></strong>
                    <p class="mb-0"><%= m.getContent() %></p>
                    <small class="text-muted"><%= m.getCreatedAt() %></small>
                </div>
            <% } %>
        <% } %>
    </div>

</body>
</html>