package controller;

import dao.JadwalDAO;
import database.Koneksi;
import model.Jadwal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class JadwalController {

    private final JadwalDAO jadwalDAO = new JadwalDAO();

    /** Memuat seluruh jadwal ke JTable. */
    public void isiTabelJadwal(DefaultTableModel model) {
        model.setRowCount(0);
        List<Jadwal> list = jadwalDAO.getAll();
        for (Jadwal j : list) {
            model.addRow(new Object[]{
                j.getId(), j.getNamaFilm(), j.getStudioId(),
                j.getTanggal(), j.getJamMulai(), j.getJamSelesai()
            });
        }
    }

    /**
     * Menambahkan jadwal baru dengan pengecekan bentrok waktu.
     *
     * [PERBAIKAN] Pesan bentrok kini muncul sebagai dialog, bukan System.out.println.
     * [PERBAIKAN] Validasi field wajib tidak kosong.
     * [PERBAIKAN] Error SQL sekarang di-log dengan pesan informatif.
     *
     * @return true  → berhasil ditambahkan
     *         false → bentrok, input kosong, atau error DB
     */
    public boolean tambahJadwal(int filmId, int studioId, String tgl, String mulai, String selesai) {
        // [PERBAIKAN] Validasi field tidak boleh kosong
        if (tgl == null || tgl.trim().isEmpty()
                || mulai == null || mulai.trim().isEmpty()
                || selesai == null || selesai.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Tanggal, jam mulai, dan jam selesai tidak boleh kosong!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // [PERBAIKAN] Validasi jam mulai harus sebelum jam selesai
        if (mulai.compareTo(selesai) >= 0) {
            JOptionPane.showMessageDialog(null,
                "Jam mulai harus lebih awal dari jam selesai!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Query cek bentrok: apakah ada jadwal lain di studio & hari yang sama
        // yang waktunya tumpang tindih dengan [mulai, selesai]
        String checkSql = "SELECT COUNT(*) FROM jadwal "
                        + "WHERE id_studio = ? AND tanggal = ? "
                        + "AND (jam_mulai < ? AND jam_selesai > ?)";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {

            ps.setInt(1, studioId);
            ps.setString(2, tgl.trim());
            ps.setString(3, selesai.trim()); // jadwal lain mulai sebelum jam selesai baru
            ps.setString(4, mulai.trim());   // jadwal lain selesai setelah jam mulai baru

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // [PERBAIKAN] Tampilkan dialog, bukan hanya System.out.println
                    JOptionPane.showMessageDialog(null,
                        "Jadwal bentrok! Studio " + studioId + " sudah ada acara\n"
                        + "pada tanggal " + tgl + " di jam tersebut.",
                        "Jadwal Bentrok", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // Tidak bentrok — insert jadwal baru
            Jadwal j = new Jadwal();
            j.setFilmId(filmId);
            j.setStudioId(studioId);
            j.setTanggal(tgl.trim());
            j.setJamMulai(mulai.trim());
            j.setJamSelesai(selesai.trim());
            return jadwalDAO.insert(j);

        } catch (SQLException e) {
            // [PERBAIKAN] Log error yang informatif
            System.err.println("JadwalController.tambahJadwal() error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Terjadi error saat menyimpan jadwal.\nCoba lagi atau hubungi admin.",
                "Error Database", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}