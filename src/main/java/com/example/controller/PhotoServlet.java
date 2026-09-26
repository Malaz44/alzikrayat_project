package com.example.controller;

import com.example.model.Photo;
import com.example.util.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/photo/*")
@MultipartConfig
public class PhotoServlet extends HttpServlet {

    private static final Pattern SHOW_PATTERN = Pattern.compile("^/(\\d+)$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^/(\\d+)/delete$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo(); // everything after /photo

        if (pathInfo == null || pathInfo.equals("/")) {
            showGallery(request, response);
            return;
        }

        if (pathInfo.equals("/upload")) {
            showUploadForm(request, response);
            return;
        }

        Matcher showMatcher = SHOW_PATTERN.matcher(pathInfo);
        if (showMatcher.matches()) {
            int photoId = Integer.parseInt(showMatcher.group(1));
            showPhotoDetails(photoId, request, response);
            return;
        }

        Matcher deleteMatcher = DELETE_PATTERN.matcher(pathInfo);
        if (deleteMatcher.matches()) {
            int photoId = Integer.parseInt(deleteMatcher.group(1));
            deletePhoto(photoId, request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page Not Found");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        storePhoto(request, response);
    }

    private void showGallery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Photo> photos = new ArrayList<>();
        String sql = "SELECT * FROM photos ORDER BY date_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                photos.add(mapRowToPhoto(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("photos", photos);
        request.getRequestDispatcher("/views/photos/gallery.jsp").forward(request, response);
    }

    private void showUploadForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/views/photos/upload.jsp").forward(request, response);
    }

   private void showPhotoDetails(int photoId, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    Photo photo = null;
    String sql = "SELECT * FROM photos WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, photoId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                photo = mapRowToPhoto(rs);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    if (photo == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Photo not found");
        return;
    }

    List<com.example.model.Comment> comments = new ArrayList<>();
    String commentSql = "SELECT c.*, u.f_name, u.l_name FROM comments c " +
                         "JOIN users u ON c.user_id = u.id " +
                         "WHERE c.photo_id = ? ORDER BY c.date_time ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(commentSql)) {

        stmt.setInt(1, photoId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                com.example.model.Comment c = new com.example.model.Comment();
                c.setId(rs.getInt("id"));
                c.setPhotoId(rs.getInt("photo_id"));
                c.setUserId(rs.getInt("user_id"));
                c.setUserName(rs.getString("f_name") + " " + rs.getString("l_name"));
                c.setComment(rs.getString("comment"));
                c.setDateTime(rs.getTimestamp("date_time"));
                comments.add(c);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    request.setAttribute("photo", photo);
    request.setAttribute("comments", comments);
    request.getRequestDispatcher("/views/photos/show.jsp").forward(request, response);
}

    private void storePhoto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        Part filePart = request.getPart("photoFile");
        String originalFileName = extractFileName(filePart);

        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;

        String uploadDirPath = getServletContext().getRealPath("/uploads");
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(uploadDirPath, fileName));
        }

        String sql = "INSERT INTO photos (user_id, file_name, title, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, fileName);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/photo");
    }

    private void deletePhoto(int photoId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        int ownerId = -1;
        String fileName = null;

        String checkSql = "SELECT user_id, file_name FROM photos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setInt(1, photoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ownerId = rs.getInt("user_id");
                    fileName = rs.getString("file_name");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (ownerId != userId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only delete your own photos");
            return;
        }

        String deleteSql = "DELETE FROM photos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setInt(1, photoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (fileName != null) {
            String uploadDirPath = getServletContext().getRealPath("/uploads");
            File fileToDelete = new File(uploadDirPath, fileName);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }

        response.sendRedirect(request.getContextPath() + "/photo");
    }

    private Photo mapRowToPhoto(ResultSet rs) throws SQLException {
        Photo photo = new Photo();
        photo.setId(rs.getInt("id"));
        photo.setUserId(rs.getInt("user_id"));
        photo.setFileName(rs.getString("file_name"));
        photo.setTitle(rs.getString("title"));
        photo.setDescription(rs.getString("description"));
        photo.setDateTime(rs.getTimestamp("date_time"));
        return photo;
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown_file";
    }
}