package com.example.util;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ تم الاتصال بقاعدة البيانات بنجاح!");
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("❌ فشل الاتصال بقاعدة البيانات:");
            e.printStackTrace();
        }
    }
}