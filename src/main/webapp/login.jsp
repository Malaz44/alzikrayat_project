<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Alzikrayat Login</title>
    <link rel="stylesheet" href="http://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>

<body>
<div class="card">
    <h2>Login to Alzikrayat</h2> 
    <% if (request.getParameter("error") != null) { %>
        <p style="color: red; text-align: center;">Invalid email or password!</p>
    <% } %>
    
    <% if (request.getParameter("success") != null) { %>
        <p style="color: green; text-align: center;">Account created successfully!</p>
    <% } %>

    <form action="auth" method="post">
        <input type="hidden" name="action" value="login">
        <div class="form-group">
            <label>Email Address:</label>
            <input type="email" name="email" required>
        </div>
        <div class="form-group">
            <label>Password:</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit">Login</button>
    </form>

    <a href="register.jsp" class="link">Don't have an account? Register here</a>
</div>

</body>
</html>