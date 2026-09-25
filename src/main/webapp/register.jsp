<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title> Alzikrayat Register</title>
    <link rel="stylesheet" href="http://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>

<body>
<div class="card">
    <h2>Create New Account</h2>
    
    <% if (request.getAttribute("error") != null) { %>
        <p style="color: red; text-align: center;"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="auth" method="post">
        <input type="hidden" name="action" value="register">
        
        <div class="form-group">
            <label>first name:</label>
            <input type="text" name="f_name" required>
        </div>
         <div class="form-group">
            <label>last name:</label>
            <input type="text" name="l_name" required>
        </div>
        <div class="form-group">
            <label>Email Address:</label>
            <input type="email" name="email" required>
        </div>
        <div class="form-group">
            <label>Password:</label>
            <input type="password" name="password" required>
        </div>
        <button type="submit">Register</button>
    </form>

    <a href="login.jsp" class="link">Already have account? Login here</a>
</div>

</body>
</html>