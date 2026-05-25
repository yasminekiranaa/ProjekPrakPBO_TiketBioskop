package controller;

import dao.KursiDAO;
import database.Koneksi;
import model.Kursi;

import java.sql.*;
import java.util.List;

public class BookingController {

    private final KursiDAO kursiDAO = new KursiDAO();

    public List<Kursi> getDaftarKursi(int studioId, int jadwalId) {
        return kursiDAO.getKursiByStudio(studioId, jadwalId);
    }

    /**
     * Memproses booking dengan transaksi atomik.
     * Jika salah satu INSERT gagal, seluruh transaksi di-rollback
     * sehingga tidak ada data setengah-jadi di database.
     *
     * @return true  → booking berhasil disimpan
     *         false → terjadi error, tidak ada data yang tersimpan
     */
    public boolean prosesBooking(int userId, int jadwalId, List<Kursi> seats,
                                 double total, String methodName) {

        // [PERBAIKAN] Validasi awal sebelum menyentuh database
        if (seats == null || seats.isEmpty()) {
            System.err.println("BookingController: Daftar kursi kosong, booking dibatalkan.");
            return false;
        }
        if (userId <= 0 || jadwalId <= 0) {
            System.err.println("BookingController: userId atau jadwalId tidak valid.");
            return false;
        }

        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // 1. Insert ke tabel booking
            String sqlBooking = "INSERT INTO booking (id_user, payment_method, total) VALUES (?, ?, ?)";
            PreparedStatement psB = conn.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS);
            psB.setInt(1, userId);
            psB.setString(2, methodName);
            psB.setDouble(3, total);
            psB.executeUpdate();

            ResultSet rs = psB.getGeneratedKeys();
            if (!rs.next()) {
                // Gagal dapat ID booking — rollback
                conn.rollback();
                System.err.println("BookingController: Gagal mendapatkan generated key booking.");
                return false;
            }

            int bookingId = rs.getInt(1);

            // 2. Insert tiap kursi ke detail_booking
            String sqlDetail = "INSERT INTO detail_booking (id_booking, id_jadwal, id_kursi, harga, status) "
                             + "VALUES (?, ?, ?, ?, 'BOOKED')";
            PreparedStatement psD = conn.prepareStatement(sqlDetail);

            for (Kursi k : seats) {
                // [PERBAIKAN] Validasi tipe kursi tidak null sebelum equalsIgnoreCase
                double hargaSatuan;
                if (k.getTipe() != null && k.getTipe().equalsIgnoreCase("VIP")) {
                    hargaSatuan = 50000;
                } else {
                    hargaSatuan = 35000; // Harga REGULER
                }

                psD.setInt(1, bookingId);
                psD.setInt(2, jadwalId);
                psD.setInt(3, k.getId());
                psD.setDouble(4, hargaSatuan);
                psD.executeUpdate();
            }

            conn.commit(); // Semua berhasil, simpan permanen
            return true;

        } catch (SQLException e) {
            // [PERBAIKAN] Rollback jika ada error di tengah transaksi
            // Mencegah data booking tersimpan tanpa detail kursi
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("BookingController: Transaksi di-rollback karena error.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;

        } finally {
            // [PERBAIKAN] Pastikan koneksi selalu ditutup dan autoCommit dikembalikan
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}