package model;

public abstract class Pembayaran {
    protected double hargaTiket;

    public Pembayaran(double hargaTiket) {
        this.hargaTiket = hargaTiket;
    }

    // Method abstract yang wajib diimplementasikan setiap jenis pembayaran
    public abstract double hitungTotalAkhir();
    public abstract String getNamaMetode();
    public abstract double getBiayaAdmin();
}