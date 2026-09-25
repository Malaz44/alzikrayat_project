<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add New Photo</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light container mt-5">

    <h2>Add New Photo</h2>

    <form action="<%= request.getContextPath() %>/photo" method="post" enctype="multipart/form-data">
        <div class="mb-3">
            <label>Title:</label>
            <input type="text" name="title" class="form-control" required>
        </div>
        <div class="mb-3">
            <label>Description:</label>
            <textarea name="description" class="form-control" rows="3"></textarea>
        </div>
        <div class="mb-3">
            <label>Choose Photo:</label>
            <input type="file" name="photoFile" class="form-control" accept="image/*" required>
        </div>
        <button type="submit" class="btn btn-success">Upload</button>
    </form>

</body>
</html>