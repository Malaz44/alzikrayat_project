<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.model.Photo" %>
<%
    Photo photo = (Photo) request.getAttribute("photo");
    Integer userId = (Integer) session.getAttribute("userId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><%= photo.getTitle() %></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light container mt-5">

    <img src="<%= request.getContextPath() %>/uploads/<%= photo.getFileName() %>" class="img-fluid mb-3" style="max-height:500px;">

    <h2><%= photo.getTitle() %></h2>
    <p><%= photo.getDescription() %></p>
    <small class="text-muted">Uploaded: <%= photo.getDateTime() %></small>

    <% if (userId != null && userId == photo.getUserId()) { %>
        <div class="mt-3">
            <a href="<%= request.getContextPath() %>/photo/<%= photo.getId() %>/delete"
               class="btn btn-danger btn-sm"
               onclick="return confirm('Are you sure you want to delete this photo?');">Delete Photo</a>
        </div>
    <% } %>

    <a href="<%= request.getContextPath() %>/photo" class="btn btn-outline-secondary btn-sm mt-3">Back to Gallery</a>
    

</body>
</html>