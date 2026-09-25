<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.model.Photo" %>
<%
    List<Photo> photos = (List<Photo>) request.getAttribute("photos");
    String userName = (String) session.getAttribute("userName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Photo Gallery</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light container mt-5">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Photo Gallery</h2>
        <% if (userName != null) { %>
            <a href="<%= request.getContextPath() %>/photo/upload" class="btn btn-success">Add New Photo</a>
        <% } %>
    </div>

    <div class="row">
        <% if (photos == null || photos.isEmpty()) { %>
            <p class="text-muted">No photos uploaded yet!</p>
        <% } else { %>
            <% for (Photo p : photos) { %>
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <img src="<%= request.getContextPath() %>/uploads/<%= p.getFileName() %>" class="card-img-top" style="height:200px; object-fit:cover;">
                        <div class="card-body">
                            <h5 class="card-title"><%= p.getTitle() %></h5>
                            <a href="<%= request.getContextPath() %>/photo/<%= p.getId() %>" class="btn btn-outline-primary btn-sm">View</a>
                        </div>
                    </div>
                </div>
            <% } %>
        <% } %>
    </div>

</body>
</html>