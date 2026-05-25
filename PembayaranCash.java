package model;
public class PembayaranCash extends Pembayaran {
    public PembayaranCash(double hargaTiket) { super(hargaTiket); }
    @Override public double hitungTotalAkhir() { return hargaTiket; }
    @Override public String getNamaMetode() { return "CASH"; }
    @Override public double getBiayaAdmin() { return 0; }
}