package dao;

import database.Koneksi;
import model.Kursi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KursiDAO {
    public List<Kursi> getKursiByStudio(int studioId, int jadwalId) {
    List<Kursi> list = new ArrayList<>();
    // Gunakan alias k.id_kursi jika kolomnya sudah di-rename
    String sql = "SELECT k.id_kursi, k.baris, k.nomor, k.tipe, db.status " +
                 "FROM kursi k " +
                 "LEFT JOIN detail_booking db ON k.id_kursi = db.id_kursi AND db.id_jadwal = ? " +
                 "WHERE k.id_studio = ?";
                 
    try (Connection conn = Koneksi.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, jadwalId);
        ps.setInt(2, studioId);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            String status = rs.getString("status");
            // Memastikan status tidak null dan seragam (Uppercase)
            if (status == null || status.isEmpty()) {
                status = "AVAILABLE";
            } else {
                status = status.toUpperCase();
            }
            
            list.add(new Kursi(
                rs.getInt("id_kursi"), // Pastikan di DB namanya id_kursi
                rs.getString("baris"),
                rs.getInt("nomor"),
                rs.getString("tipe"),
                status
            ));
        }
    } catch (SQLException e) { 
        System.out.println("Error di KursiDAO: " + e.getMessage()); 
    }
    return list;
}
}