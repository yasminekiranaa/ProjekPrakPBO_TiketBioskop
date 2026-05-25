package model;

public class Film {
    private int id;
    private String nama;
    private int durasi;
    private String genre;

    public Film() {} // Constructor kosong

    // Getter dan Setter (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public int getDurasi() { return durasi; }
    public void setDurasi(int durasi) { this.durasi = durasi; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}