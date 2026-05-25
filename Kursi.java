package model;

public class Kursi {
    private int id;
    private int studioId;
    private String baris;
    private int nomor;
    private String tipe; 
    private String status; 

    public Kursi(int id, String baris, int nomor, String tipe, String status) {
        this.id = id;
        this.baris = baris;
        this.nomor = nomor;
        this.tipe = tipe;
        this.status = status;
    }

    // Getter
    public int getId() { return id; }
    public String getBaris() { return baris; }
    public int getNomor() { return nomor; }
    public String getTipe() { return tipe; }
    public String getStatus() { return status; }
    
    // Method untuk mendapatkan nama kursi gabungan (Contoh: A1)
    public String getNamaKursi() {
        return baris + nomor;
    }
}