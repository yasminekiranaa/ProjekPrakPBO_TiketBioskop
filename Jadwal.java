package model;

public class Jadwal {
    private int id;
    private int filmId;
    private int studioId;
    private String tanggal;
    private String jamMulai;
    private String jamSelesai;
    private String namaFilm; // Tambahan untuk memudahkan tampilan di Tabel

    public Jadwal() {}

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFilmId() { return filmId; }
    public void setFilmId(int filmId) { this.filmId = filmId; }
    public int getStudioId() { return studioId; }
    public void setStudioId(int studioId) { this.studioId = studioId; }
    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }
    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }
    public String getNamaFilm() { return namaFilm; }
    public void setNamaFilm(String namaFilm) { this.namaFilm = namaFilm; }
}