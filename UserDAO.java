package dao;

import database.Koneksi;
import java.sql.*;

public class UserDAO {
    public int loginOrRegister(String nama, String email) {
        // 1. Cek apakah user dengan email tersebut sudah ada
        String sqlCheck = "SELECT id_user FROM user WHERE email = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            
            psCheck.setString(1, email);
            ResultSet rs = psCheck.executeQuery();
            
            if (rs.next()) {
                // User sudah ada, kembalikan ID-nya
                return rs.getInt("id_user");
            } else {
                // 2. Jika belum ada, buat user baru (Sesuai kolom: nama, email)
                String sqlInsert = "INSERT INTO user (nama, email) VALUES (?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, nama);
                    psInsert.setString(2, email);
                    psInsert.executeUpdate();
                    
                    ResultSet rsKey = psInsert.getGeneratedKeys();
                    if (rsKey.next()) {
                        return rsKey.getInt(1); // Kembalikan ID yang baru dibuat
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}