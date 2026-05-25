package dao;

import database.Koneksi;
import model.Film;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    // 1. Menambahkan Film (Create)
    public boolean insert(Film film) {
        String sql = "INSERT INTO film (judul, durasi, genre) VALUES (?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, film.getNama());
            ps.setInt(2, film.getDurasi());
            ps.setString(3, film.getGenre());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Mengambil Semua Data Film (Read)
    public List<Film> getAll() {
        List<Film> listFilm = new ArrayList<>();
        String sql = "SELECT * FROM film";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("id_film"));
                film.setNama(rs.getString("judul"));
                film.setDurasi(rs.getInt("durasi"));
                film.setGenre(rs.getString("genre"));
                listFilm.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listFilm;
    }

    // 3. Mengubah Data Film (Update)
    public boolean update(Film film) {
        String sql = "UPDATE film SET judul = ?, durasi = ?, genre = ? WHERE id_film = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, film.getNama());
            ps.setInt(2, film.getDurasi());
            ps.setString(3, film.getGenre());
            ps.setInt(4, film.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Menghapus Film (Delete)
    public boolean delete(int id) {
        String sql = "DELETE FROM film WHERE id_film = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}