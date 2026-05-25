package dao;

import database.Koneksi;
import model.Jadwal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JadwalDAO {
    
    // Mengambil semua jadwal (Join dengan tabel film untuk mendapatkan nama film)
    public List<Jadwal> getAll() {
        List<Jadwal> list = new ArrayList<>();
        String sql = "SELECT j.*, f.judul AS judul FROM jadwal j JOIN film f ON j.id_film = f.id_film";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Jadwal j = new Jadwal();
                j.setId(rs.getInt("id_jadwal"));
                j.setFilmId(rs.getInt("id_film"));
                j.setStudioId(rs.getInt("id_studio"));
                j.setTanggal(rs.getString("tanggal"));
                j.setJamMulai(rs.getString("jam_mulai"));
                j.setJamSelesai(rs.getString("jam_selesai"));
                j.setNamaFilm(rs.getString("judul"));
                list.add(j);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Jadwal j) {
        String sql = "INSERT INTO jadwal (id_film, id_studio, tanggal, jam_mulai, jam_selesai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, j.getFilmId());
            ps.setInt(2, j.getStudioId());
            ps.setString(3, j.getTanggal());
            ps.setString(4, j.getJamMulai());
            ps.setString(5, j.getJamSelesai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}