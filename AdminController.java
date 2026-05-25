package controller;

import database.Koneksi;

import java.sql.*;

public class AdminController {

    /*
     * Validasi login admin ke database.
     * Catatan: Untuk keamanan lebih baik, password sebaiknya di-hash (misal MD5/BCrypt)
     * sebelum disimpan ke DB dan saat dibandingkan.
     *
     * @return true  → login berhasil
     *         false → username/password salah atau field kosong atau error DB
     */
    public boolean login(String username, String password) {
        // [PERBAIKAN] Validasi input tidak boleh kosong sebelum query ke DB
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.isEmpty()) {
            return false;
        }

        String sql = "SELECT id_admin FROM admin WHERE username = ? AND password = ?";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);

            // [PERBAIKAN] Gunakan ResultSet try-with-resources agar otomatis ditutup
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            // [PERBAIKAN] Log pesan error yang lebih informatif
            System.err.println("AdminController.login() error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}