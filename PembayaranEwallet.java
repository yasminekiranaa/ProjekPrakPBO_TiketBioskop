package model;

public class PembayaranEwallet extends Pembayaran {
    private final double BIAYA_ADMIN_PER_TIKET = 5000;
    private int jumlahTiket;

       public PembayaranEwallet(double hargaTiket, int jumlahTiket) {
        super(hargaTiket);
        this.jumlahTiket = jumlahTiket;
    }

    @Override
    public double hitungTotalAkhir() {
        // Harga tiket total + (5000 * jumlah kursi)
        return hargaTiket + (BIAYA_ADMIN_PER_TIKET * jumlahTiket);
    }

    @Override
    public String getNamaMetode() {
        return "E-WALLET";
    }

    @Override
    public double getBiayaAdmin() {
        return BIAYA_ADMIN_PER_TIKET * jumlahTiket;
    }
}