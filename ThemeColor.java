package view;

import java.awt.Color;
import java.awt.Font;

public class ThemeColor {
    // ── Warna Utama (sesuai mockup) ──────────────────────────────────────────
    public static final Color PINK_BG      = new Color(255, 209, 215);   // pink header & bg
    public static final Color PINK_LIGHT   = new Color(255, 228, 230);   // page background
    public static final Color DARK_BROWN   = new Color(74,  34,  23);    // sidebar & header gelap
    public static final Color MED_BROWN    = new Color(120,  72,  56);   // sidebar item hover
    public static final Color SIDEBAR_ACTIVE = new Color(176, 90, 110);  // sidebar item aktif (pink muda)
    public static final Color WHITE        = Color.WHITE;
    public static final Color LIGHT_GRAY   = new Color(248, 243, 244);   // background konten
    public static final Color ACCENT       = new Color(214,  51, 108);   // pink accent (tombol, link)
    public static final Color ACCENT_LIGHT = new Color(255, 192, 203);   // border pink muda
    public static final Color GREEN_BTN    = new Color(56,  189,  99);   // tombol "Beli Tiket" / "Pilih"
    public static final Color BLUE         = new Color(70,  130, 180);
    public static final Color RED          = new Color(231,  76,  60);
    public static final Color YELLOW       = new Color(255, 193,   7);
    public static final Color VIP_PINK     = new Color(255, 105, 180);

    // Warna kursi
    public static final Color SEAT_AVAILABLE     = new Color(102, 221,  80);  // hijau - reguler tersedia
    public static final Color SEAT_VIP           = new Color(255, 192, 203);  // pink - VIP tersedia
    public static final Color SEAT_SELECTED      = new Color(255, 214,  51);  // kuning - terpilih
    public static final Color SEAT_SOLD          = new Color(231,  76,  60);  // merah - terjual

    // Warna rating badge
    public static final Color BADGE_PG    = new Color(100, 170, 230);
    public static final Color BADGE_17    = new Color(180,  60,  60);
    public static final Color BADGE_SU    = new Color(150, 200, 130);
    public static final Color BADGE_GRAY  = new Color(150, 150, 150);

    // ── Font ─────────────────────────────────────────────────────────────────
    public static Font fontBold(int size)  { return new Font("Segoe UI", Font.BOLD,  size); }
    public static Font fontPlain(int size) { return new Font("Segoe UI", Font.PLAIN, size); }
    public static Font fontItalic(int size){ return new Font("Segoe UI", Font.ITALIC, size); }
}