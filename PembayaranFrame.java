package view;

import controller.BookingController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PembayaranFrame extends JFrame {

    private int userId, jadwalId, idStudio;
    private List<Kursi> selectedSeats;
    private String judulFilm;
    private double hargaTiketDasar = 0;

    // Komponen yang perlu diupdate
    private JLabel lblTotal;
    private JPanel contentArea;
    private CardLayout contentCard;

    // State
    private String metodeFinal;
    private double totalFinal;

    public PembayaranFrame(int userId, int jadwalId, int idStudio,
                           List<Kursi> seats, String judul) {
        this.userId        = userId;
        this.jadwalId      = jadwalId;
        this.idStudio      = idStudio;
        this.selectedSeats = seats;
        this.judulFilm     = judul;

        // Hitung harga dasar semua kursi
        for (Kursi k : seats) {
            hargaTiketDasar += k.getTipe().equalsIgnoreCase("VIP") ? 50000 : 35000;
        }

        setTitle("Pembayaran - Bioskop Inces");
        setSize(900, 640);
        // [PERBAIKAN] DO_NOTHING_ON_CLOSE — jangan tutup otomatis saat X diklik,
        // biar kita bisa tanya konfirmasi dulu lewat navbar logout
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeColor.LIGHT_GRAY);

        // [PERBAIKAN] Tombol logout navbar sekarang kembali ke UserMainPage,
        // bukan tutup program. Tampilkan dialog konfirmasi sebelum keluar.
        NavbarPanel navbar = new NavbarPanel("User", this::konfirmasiKeluar);
        root.add(navbar, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(ThemeColor.LIGHT_GRAY);

        String[][] menus = {{"jadwal", "📅", "Jadwal"}};
        SidebarPanel sidebar = new SidebarPanel(menus, "jadwal", k -> {});
        body.add(sidebar, BorderLayout.WEST);

        // Card layout: detail → pilih metode bayar → konfirmasi sukses
        contentCard = new CardLayout();
        contentArea = new JPanel(contentCard);
        contentArea.setBackground(ThemeColor.LIGHT_GRAY);
        contentArea.add(buildDetailPanel(),       "detail");
        contentArea.add(buildPembayaranPanel(),   "bayar");
        contentArea.add(buildKonfirmasiPanel(),   "konfirmasi");

        body.add(contentArea, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        add(root);
    }

    // ─── Konfirmasi keluar (dipakai navbar & tombol kembali) ─────────────────
    /**
     * [PERBAIKAN] Sebelumnya navbar langsung dispose() → program mati.
     * Sekarang muncul dialog konfirmasi; jika Ya, kembali ke UserMainPage.
     */
    private void konfirmasiKeluar() {
        int pilihan = JOptionPane.showConfirmDialog(this,
            "Batalkan pembayaran dan kembali ke halaman utama?",
            "Konfirmasi Keluar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (pilihan == JOptionPane.YES_OPTION) {
            kembaliKeHalamanUtama();
        }
        // Kalau NO → tidak melakukan apa-apa, tetap di halaman ini
    }

    /**
     * [PERBAIKAN] Metode terpusat untuk kembali ke UserMainPage.
     * Dipanggil setelah cetak tiket ATAU saat user memilih keluar.
     */
    private void kembaliKeHalamanUtama() {
        new UserMainPage(userId).setVisible(true);
        this.dispose();
    }

    // ─── Panel 1: Detail Pesanan ──────────────────────────────────────────────
    private JPanel buildDetailPanel() {
        JPanel p = wrapPanel("Detail Pesanan");

        JPanel card = buildDarkCard();
        card.setLayout(new BorderLayout(0, 16));

        JLabel titleCard = new JLabel("Detail Pesanan", SwingConstants.CENTER);
        titleCard.setFont(ThemeColor.fontBold(16));
        titleCard.setForeground(ThemeColor.ACCENT_LIGHT);
        titleCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel info = new JPanel(new GridLayout(0, 2, 10, 12));
        info.setOpaque(false);
        addInfoRow(info, "Film",   judulFilm);
        addInfoRow(info, "Studio", "Studio " + idStudio);
        addInfoRow(info, "Kursi",  buildKursiStr());
        addInfoRow(info, "Total",  formatRp(hargaTiketDasar));

        RoundedButton btnNext = new RoundedButton("Lanjut ke Pembayaran  ➜", ThemeColor.ACCENT, 8);
        btnNext.setFont(ThemeColor.fontBold(14));
        btnNext.addActionListener(e -> {
            updateTotalLabel();
            contentCard.show(contentArea, "bayar");
        });

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.setOpaque(false);
        btnWrap.add(btnNext);

        card.add(titleCard, BorderLayout.NORTH);
        card.add(info,      BorderLayout.CENTER);
        card.add(btnWrap,   BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = 120; gbc.ipady = 40;
        center.add(card, gbc);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ─── Panel 2: Metode Pembayaran ───────────────────────────────────────────
    private JPanel buildPembayaranPanel() {
        JPanel p = wrapPanel("Pembayaran");

        JPanel card = buildDarkCard();
        card.setLayout(new BorderLayout(0, 16));

        JLabel titleCard = new JLabel("Metode Pembayaran", SwingConstants.CENTER);
        titleCard.setFont(ThemeColor.fontBold(16));
        titleCard.setForeground(ThemeColor.ACCENT_LIGHT);
        titleCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

        ButtonGroup group = new ButtonGroup();
        JRadioButton rbCash    = buildRadio("Cash");
        JRadioButton rbEwallet = buildRadio("E-Wallet (OVO, DANA, GOPAY)");
        rbCash.setSelected(true);
        group.add(rbCash);
        group.add(rbEwallet);
        radioPanel.add(rbCash);
        radioPanel.add(Box.createVerticalStrut(12));
        radioPanel.add(rbEwallet);

        lblTotal = new JLabel(formatRp(hargaTiketDasar), SwingConstants.RIGHT);
        lblTotal.setFont(ThemeColor.fontBold(18));
        lblTotal.setForeground(new Color(244, 143, 177));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColor.MED_BROWN));
        JLabel lblTotalLabel = new JLabel("Total Bayar:");
        lblTotalLabel.setFont(ThemeColor.fontBold(14));
        lblTotalLabel.setForeground(ThemeColor.ACCENT_LIGHT);
        totalRow.add(lblTotalLabel, BorderLayout.WEST);
        totalRow.add(lblTotal,      BorderLayout.EAST);

        rbEwallet.addActionListener(e -> {
            double admin = 5000 * selectedSeats.size();
            lblTotal.setText(formatRp(hargaTiketDasar + admin));
        });
        rbCash.addActionListener(e -> lblTotal.setText(formatRp(hargaTiketDasar)));

        RoundedButton btnBayar = new RoundedButton("Konfirmasi Pembayaran", ThemeColor.ACCENT, 8);
        btnBayar.setFont(ThemeColor.fontBold(14));
        btnBayar.addActionListener(e -> {
            boolean isEwallet = rbEwallet.isSelected();
            Pembayaran payment = isEwallet
                ? new PembayaranEwallet(hargaTiketDasar, selectedSeats.size())
                : new PembayaranCash(hargaTiketDasar);

            totalFinal  = payment.hitungTotalAkhir();
            metodeFinal = payment.getNamaMetode();

            boolean sukses = new BookingController().prosesBooking(
                userId, jadwalId, selectedSeats, totalFinal, metodeFinal);

            if (sukses) {
                rebuildKonfirmasi();
                contentCard.show(contentArea, "konfirmasi");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal memproses transaksi.\nCoba lagi atau hubungi admin.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // [PERBAIKAN] Tombol Kembali ke halaman detail, bukan menutup program
        RoundedButton btnKembali = new RoundedButton("← Kembali", ThemeColor.MED_BROWN, 8);
        btnKembali.setFont(ThemeColor.fontBold(13));
        btnKembali.addActionListener(e -> contentCard.show(contentArea, "detail"));

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(btnKembali);
        btnWrap.add(btnBayar);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.add(radioPanel);
        inner.add(Box.createVerticalStrut(20));
        inner.add(totalRow);

        card.add(titleCard, BorderLayout.NORTH);
        card.add(inner,     BorderLayout.CENTER);
        card.add(btnWrap,   BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = 100; gbc.ipady = 40;
        center.add(card, gbc);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ─── Panel 3: Konfirmasi Sukses ───────────────────────────────────────────
    private JPanel buildKonfirmasiPanel() {
        // Placeholder — akan di-rebuild setelah bayar berhasil
        return wrapPanel("Konfirmasi");
    }

    private void rebuildKonfirmasi() {
        contentArea.remove(2);

        JPanel p = wrapPanel("Booking Berhasil! 🎉");

        JPanel card = buildDarkCard();
        card.setLayout(new BorderLayout(0, 16));

        JLabel titleCard = new JLabel("Detail Pesanan", SwingConstants.CENTER);
        titleCard.setFont(ThemeColor.fontBold(16));
        titleCard.setForeground(ThemeColor.ACCENT_LIGHT);
        titleCard.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel info = new JPanel(new GridLayout(0, 2, 10, 12));
        info.setOpaque(false);
        addInfoRow(info, "Film",   judulFilm);
        addInfoRow(info, "Studio", "Studio " + idStudio);
        addInfoRow(info, "Kursi",  buildKursiStr());
        addInfoRow(info, "Metode", metodeFinal);
        addInfoRow(info, "Total",  formatRp(totalFinal));

        // [PERBAIKAN] Pisah tombol "Cetak Tiket" dan "Kembali ke Beranda"
        // agar user bisa cetak dulu, lalu kembali sendiri tanpa program mati

        RoundedButton btnCetak = new RoundedButton("🖨  Cetak Tiket", ThemeColor.ACCENT, 8);
        btnCetak.setFont(ThemeColor.fontBold(14));
        btnCetak.addActionListener(e -> {
            double adminPerTiket = metodeFinal.equalsIgnoreCase("E-WALLET") ? 5000 : 0;
            cetakTiket(adminPerTiket, metodeFinal);
            // [PERBAIKAN] Tidak langsung dispose() — user tetap di halaman konfirmasi
            // Bisa cetak ulang atau baru kembali ke beranda lewat tombol di bawah
        });

        RoundedButton btnBeranda = new RoundedButton("🏠  Kembali ke Beranda", ThemeColor.MED_BROWN, 8);
        btnBeranda.setFont(ThemeColor.fontBold(14));
        btnBeranda.addActionListener(e -> kembaliKeHalamanUtama());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(btnCetak);
        btnWrap.add(btnBeranda);

        card.add(titleCard, BorderLayout.NORTH);
        card.add(info,      BorderLayout.CENTER);
        card.add(btnWrap,   BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = 120; gbc.ipady = 40;
        center.add(card, gbc);

        p.add(center, BorderLayout.CENTER);

        contentArea.add(p, "konfirmasi");
        contentArea.revalidate();
    }

    // ─── Cetak Tiket ─────────────────────────────────────────────────────────
    private void cetakTiket(double adminPerTiket, String metode) {
        for (Kursi k : selectedSeats) {
            double hargaDasar    = k.getTipe().equalsIgnoreCase("VIP") ? 50000 : 35000;
            double totalPerTiket = hargaDasar + adminPerTiket;

            StringBuilder sb = new StringBuilder();
            sb.append("==========================================\n");
            sb.append("           TIKET BIOSKOP INCES           \n");
            sb.append("==========================================\n");
            sb.append(String.format(" Film    : %s\n",           judulFilm));
            sb.append(String.format(" Studio  : Studio %d\n",    idStudio));
            sb.append(String.format(" Kursi   : %s (%s)\n",      k.getNamaKursi(), k.getTipe().toUpperCase()));
            sb.append(String.format(" ID User : %d\n",           userId));
            sb.append("------------------------------------------\n");
            sb.append(String.format(" Harga   : Rp %,.0f\n",     hargaDasar));
            sb.append(String.format(" Admin   : Rp %,.0f\n",     adminPerTiket));
            sb.append(String.format(" TOTAL   : Rp %,.0f\n",     totalPerTiket));
            sb.append("------------------------------------------\n");
            sb.append(String.format(" Status  : LUNAS (%s)\n",   metode.toUpperCase()));
            sb.append("==========================================\n");
            sb.append("    ENJOY THE MOVIE - BIOSKOP INCES      \n");

            JTextArea area = new JTextArea(sb.toString());
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            area.setEditable(false);
            area.setBackground(new Color(245, 245, 245));
            area.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            // [PERBAIKAN] Dialog tiket pakai parent=this sehingga
            // jendela utama tidak ikut tertutup saat dialog ditutup
            JOptionPane.showMessageDialog(this,
                new JScrollPane(area),
                "🎟 Tiket - " + k.getNamaKursi(),
                JOptionPane.PLAIN_MESSAGE);
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private JPanel wrapPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(ThemeColor.LIGHT_GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel lbl = new JLabel(title);
        lbl.setFont(ThemeColor.fontBold(20));
        lbl.setForeground(ThemeColor.DARK_BROWN);
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildDarkCard() {
        JPanel card = new JPanel();
        card.setBackground(ThemeColor.DARK_BROWN);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(14, ThemeColor.DARK_BROWN),
            BorderFactory.createEmptyBorder(24, 30, 24, 30)
        ));
        return card;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeColor.fontBold(13));
        lbl.setForeground(ThemeColor.ACCENT_LIGHT);

        JLabel val = new JLabel(value, SwingConstants.RIGHT);
        val.setFont(ThemeColor.fontBold(13));
        val.setForeground(ThemeColor.WHITE);

        panel.add(lbl);
        panel.add(val);
    }

    private JRadioButton buildRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(ThemeColor.fontPlain(14));
        rb.setForeground(ThemeColor.WHITE);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        return rb;
    }

    private String buildKursiStr() {
        StringBuilder sb = new StringBuilder();
        for (Kursi k : selectedSeats) sb.append(k.getNamaKursi()).append(" ");
        return sb.toString().trim();
    }

    private String formatRp(double amount) {
        return String.format("Rp %,.0f", amount);
    }

    private void updateTotalLabel() {
        if (lblTotal != null) lblTotal.setText(formatRp(hargaTiketDasar));
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int   radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }

        @Override public Insets  getBorderInsets(Component c) { return new Insets(radius, radius, radius, radius); }
        @Override public boolean isBorderOpaque()              { return false; }
    }
}