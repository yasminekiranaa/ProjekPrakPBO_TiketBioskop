package controller;

import dao.FilmDAO;
import model.Film;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class FilmController {

    private final FilmDAO filmDAO;

    public FilmController() {
        this.filmDAO = new FilmDAO();
    }

    public List<Film> getAllFilm() {
        return filmDAO.getAll();
    }

    /** Memuat seluruh data film dari database ke JTable. */
    public void isiTabelFilm(DefaultTableModel model) {
        model.setRowCount(0);
        List<Film> list = filmDAO.getAll();
        for (Film f : list) {
            model.addRow(new Object[]{f.getId(), f.getNama(), f.getDurasi(), f.getGenre()});
        }
    }

    /**
     * Menambahkan film baru.
     *
     * @return true  → berhasil
     *         false → gagal (input tidak valid atau error DB)
     */
    public boolean tambahFilm(String nama, String durasi, String genre) {
        // [PERBAIKAN] Validasi field tidak boleh kosong
        if (nama == null || nama.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Nama film tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (genre == null || genre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Genre tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // [PERBAIKAN] try-catch untuk parseInt agar tidak crash jika input bukan angka
        int durasiInt;
        try {
            durasiInt = Integer.parseInt(durasi.trim());
            if (durasiInt <= 0) {
                JOptionPane.showMessageDialog(null,
                    "Durasi harus berupa angka positif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                "Durasi harus berupa angka! (contoh: 120)", "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Film f = new Film();
        f.setNama(nama.trim());
        f.setDurasi(durasiInt);
        f.setGenre(genre.trim());
        return filmDAO.insert(f);
    }

    /**
     * Mengubah data film yang sudah ada.
     *
     * @return true  → berhasil
     *         false → gagal (input tidak valid atau error DB)
     */
    public boolean ubahFilm(int id, String nama, String durasi, String genre) {
        // [PERBAIKAN] Validasi field tidak boleh kosong
        if (nama == null || nama.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Nama film tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (genre == null || genre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Genre tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // [PERBAIKAN] try-catch untuk parseInt
        int durasiInt;
        try {
            durasiInt = Integer.parseInt(durasi.trim());
            if (durasiInt <= 0) {
                JOptionPane.showMessageDialog(null,
                    "Durasi harus berupa angka positif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                "Durasi harus berupa angka! (contoh: 120)", "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Film f = new Film();
        f.setId(id);
        f.setNama(nama.trim());
        f.setDurasi(durasiInt);
        f.setGenre(genre.trim());
        return filmDAO.update(f);
    }

    /**
     * Menghapus film berdasarkan ID.
     * Menampilkan dialog konfirmasi sebelum menghapus.
     *
     * @return true  → dihapus
     *         false → dibatalkan atau error
     */
    public boolean hapusFilm(int id) {
        // [PERBAIKAN] Konfirmasi sebelum hapus agar tidak terhapus tidak sengaja
        int konfirmasi = JOptionPane.showConfirmDialog(null,
            "Yakin ingin menghapus film ini?\nData yang sudah dihapus tidak bisa dikembalikan.",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return false; // User membatalkan
        }

        return filmDAO.delete(id);
    }
}