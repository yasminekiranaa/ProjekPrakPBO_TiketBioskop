package view;

import controller.JadwalController;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class UserMainPage extends JFrame {

    private JadwalController  jadwalController = new JadwalController();
    private JTable            tableJadwal;
    private DefaultTableModel modelJadwal;
    private int               currentUserId;
    private JTextField        txtCariFilm;

    private JPanel      contentArea;
    private CardLayout  contentCard;
    private SidebarPanel sidebar;

    public UserMainPage(int userId) {
        this.currentUserId = userId;
        setTitle("Bioskop Inces");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeColor.WHITE);

        // Navbar
        root.add(new NavbarPanel("User", this::doLogout), BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(ThemeColor.WHITE);

        // Sidebar — 3 menu sesuai mockup
        String[][] menus = {
            {"dashboard", "🕐", "Dashboard"},
            {"tiket",     "🎫", "Tiket Saya"},
            {"jadwal",    "📅", "Jadwal"},
        };
        sidebar = new SidebarPanel(menus, "dashboard", key -> {
            contentCard.show(contentArea, key);
            sidebar.setActive(key);
        });
        body.add(sidebar, BorderLayout.WEST);

        // Content
        contentCard = new CardLayout();
        contentArea = new JPanel(contentCard);
        contentArea.setBackground(ThemeColor.WHITE);
        contentArea.add(buildDashboard(), "dashboard");
        contentArea.add(buildTiket(),     "tiket");
        contentArea.add(buildJadwal(),    "jadwal");

        body.add(contentArea, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        jadwalController.isiTabelJadwal(modelJadwal);
        setupSearch();
    }

    // ═══════════════════════════════════════════════════════
    //  DASHBOARD — 2×2 film cards
    // ═══════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(ThemeColor.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("Film Sedang Tayang");
        title.setFont(ThemeColor.fontBold(20));
        title.setForeground(ThemeColor.DARK_BROWN);
        p.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        // Data film sesuai mockup (poster placeholder warna, judul, genre, badge, durasi, harga)
        Object[][] films = {
            {"Petualangan Sherina 2", "Musikal / Petualangan", "PG",  "2j 15m", "50.000", new Color(190, 130, 140)},
            {"KKN di Desa Penari",   "Horor / Thriller",      "17+", "1j 58m", "45.000", new Color(150,  90, 170)},
            {"Oppenheimer",          "Darama / Sejarah",      "17+", "3j 0m",  "65.000", new Color(140, 160, 210)},
            {"Elemental",            "Animasi / Keluarga",    "SU",  "1j 42m", "55.000", new Color(230, 140,  60)},
        };

        for (Object[] f : films) grid.add(filmCard(f));
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    /** Kartu film — persis mockup: poster placeholder atas, info bawah, background coklat gelap */
    private JPanel filmCard(Object[] f) {
        String judul  = (String) f[0];
        String genre  = (String) f[1];
        String rating = (String) f[2];
        String durasi = (String) f[3];
        String harga  = (String) f[4];
        Color  clr    = (Color)  f[5];

        // Card wrapper — rounded coklat gelap
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.DARK_BROWN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        // ── Poster placeholder (atas) ──
        JPanel poster = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Rounded only on top corners
                g2.setColor(clr);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 14, 14, 14);
                // Teks placeholder
                g2.setFont(ThemeColor.fontPlain(12));
                g2.setColor(new Color(255, 255, 255, 180));
                FontMetrics fm = g2.getFontMetrics();
                String ph = "(poster film)";
                g2.drawString(ph, (getWidth() - fm.stringWidth(ph)) / 2,
                              (getHeight() + fm.getAscent()) / 2 - 4);
                g2.dispose();
            }
        };
        poster.setOpaque(false);
        poster.setPreferredSize(new Dimension(0, 110));

        // ── Info panel (bawah) ──
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(8, 14, 14, 14));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(ThemeColor.fontBold(13));
        lblJudul.setForeground(Color.WHITE);

        JLabel lblGenre = new JLabel(genre);
        lblGenre.setFont(ThemeColor.fontPlain(11));
        lblGenre.setForeground(new Color(210, 185, 185));

        // Badge row: rating + durasi
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(ratingBadge(rating));
        badgeRow.add(grayBadge(durasi));

        JLabel lblHarga = new JLabel("Rp " + harga);
        lblHarga.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHarga.setForeground(new Color(255, 90, 90));

        // Tombol Beli Tiket — hijau, full width
        JButton btnBeli = new JButton("Beli Tiket") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.GREEN_BTN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnBeli.setFont(ThemeColor.fontBold(12));
        btnBeli.setForeground(Color.WHITE);
        btnBeli.setOpaque(false);
        btnBeli.setContentAreaFilled(false);
        btnBeli.setBorderPainted(false);
        btnBeli.setFocusPainted(false);
        btnBeli.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBeli.setPreferredSize(new Dimension(100, 30));
        btnBeli.setMaximumSize(new Dimension(120, 30));
        btnBeli.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBeli.addActionListener(e -> {
            sidebar.setActive("jadwal");
            contentCard.show(contentArea, "jadwal");
        });

        info.add(lblJudul);
        info.add(Box.createVerticalStrut(2));
        info.add(lblGenre);
        info.add(Box.createVerticalStrut(6));
        info.add(badgeRow);
        info.add(Box.createVerticalStrut(6));
        info.add(lblHarga);
        info.add(Box.createVerticalStrut(8));
        info.add(btnBeli);

        card.add(poster, BorderLayout.NORTH);
        card.add(info,   BorderLayout.CENTER);
        return card;
    }

    private JLabel ratingBadge(String text) {
        Color c = "PG".equals(text)   ? ThemeColor.BADGE_PG
                : text.contains("17") ? ThemeColor.BADGE_17
                : "SU".equals(text)   ? ThemeColor.BADGE_SU
                : ThemeColor.BADGE_GRAY;
        return badge(text, c);
    }

    private JLabel grayBadge(String text) { return badge(text, ThemeColor.BADGE_GRAY); }

    private JLabel badge(String text, Color bg) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(ThemeColor.fontBold(10));
        l.setForeground(Color.WHITE);
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        return l;
    }

    // ═══════════════════════════════════════════════════════
    //  TIKET SAYA — list tiket sesuai mockup
    // ═══════════════════════════════════════════════════════
    private JPanel buildTiket() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(ThemeColor.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("Tiket Saya");
        title.setFont(ThemeColor.fontBold(20));
        title.setForeground(ThemeColor.DARK_BROWN);

        JLabel sub = new JLabel("Berikut adalah tiket yang sudah kamu beli:");
        sub.setFont(ThemeColor.fontPlain(13));
        sub.setForeground(new Color(120, 100, 95));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        // Contoh data tiket sesuai mockup
        Object[][] tickets = {
            {"Petualangan Serinna 2", "Senin, 11 Mei 2025",  "13:00", "Studio 1", "A5, A6",  false},
            {"KKN Di Desa Penari",   "Selasa, 12 Mei 2026", "19:30", "Studio 2", "B3, B4",  true},
            {"Oppenhelmer",          "Rabu, 13 Mei 2026",   "20:00", "Studio 3", "C7, C8",  true},
            {"Elemental",            "Kamis, 14 Mei 2026",  "18:30", "Studio 1", "D1, D2",  true},
        };

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Object[] t : tickets) {
            list.add(tiketRow(t));
            list.add(Box.createVerticalStrut(12));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        p.add(header, BorderLayout.NORTH);
        p.add(scroll,  BorderLayout.CENTER);
        return p;
    }

    /** Satu baris tiket — background coklat gelap, sesuai mockup */
    private JPanel tiketRow(Object[] t) {
        String judul   = (String)  t[0];
        String tanggal = (String)  t[1];
        String jam     = (String)  t[2];
        String studio  = (String)  t[3];
        String kursi   = (String)  t[4];
        boolean selesai = (Boolean) t[5];

        JPanel row = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.DARK_BROWN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Poster kecil placeholder
        JPanel thumb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        thumb.setOpaque(false);
        thumb.setPreferredSize(new Dimension(50, 50));

        // Info tengah
        JPanel mid = new JPanel(new GridLayout(1, 5, 8, 0));
        mid.setOpaque(false);

        JPanel leftInfo = new JPanel();
        leftInfo.setOpaque(false);
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(ThemeColor.fontBold(13));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblTgl = new JLabel(tanggal);
        lblTgl.setFont(ThemeColor.fontPlain(11));
        lblTgl.setForeground(new Color(200, 180, 175));
        leftInfo.add(lblJudul);
        leftInfo.add(lblTgl);

        mid.add(leftInfo);
        mid.add(centeredWhite(jam));
        mid.add(centeredWhite(studio));
        mid.add(centeredWhite(kursi));

        // Kanan: badge Selesai + tombol Lihat Tiket
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        if (selesai) {
            JLabel lblSelesai = new JLabel("Selesai");
            lblSelesai.setFont(ThemeColor.fontBold(11));
            lblSelesai.setForeground(ThemeColor.GREEN_BTN);
            lblSelesai.setAlignmentX(Component.RIGHT_ALIGNMENT);
            right.add(lblSelesai);
            right.add(Box.createVerticalStrut(6));
        }

        JButton btnLihat = new JButton("Lihat Tiket") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLihat.setFont(ThemeColor.fontBold(11));
        btnLihat.setForeground(Color.WHITE);
        btnLihat.setOpaque(false);
        btnLihat.setContentAreaFilled(false);
        btnLihat.setBorderPainted(false);
        btnLihat.setFocusPainted(false);
        btnLihat.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnLihat.setMaximumSize(new Dimension(100, 28));
        btnLihat.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        right.add(btnLihat);

        row.add(thumb, BorderLayout.WEST);
        row.add(mid,   BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JLabel centeredWhite(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(ThemeColor.fontPlain(12));
        l.setForeground(Color.WHITE);
        return l;
    }

    // ═══════════════════════════════════════════════════════
    //  JADWAL — tabel + filter
    // ═══════════════════════════════════════════════════════
    private JPanel buildJadwal() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(ThemeColor.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("Jadwal Tayang");
        title.setFont(ThemeColor.fontBold(20));
        title.setForeground(ThemeColor.DARK_BROWN);

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setOpaque(false);

        JLabel lblTgl    = new JLabel("Pilih Tanggal");
        JLabel lblStudio = new JLabel("Pilih Studio");
        lblTgl.setFont(ThemeColor.fontPlain(12));
        lblStudio.setFont(ThemeColor.fontPlain(12));
        lblTgl.setForeground(ThemeColor.DARK_BROWN);
        lblStudio.setForeground(ThemeColor.DARK_BROWN);

        filterRow.add(lblTgl);
        filterRow.add(dropdownBtn("11 Mei 2026  ▾"));
        filterRow.add(lblStudio);
        filterRow.add(dropdownBtn("Semua Studio  ▾"));

        txtCariFilm = new JTextField(16);
        txtCariFilm.setFont(ThemeColor.fontPlain(13));
        txtCariFilm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.ACCENT_LIGHT, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtCariFilm.setPreferredSize(new Dimension(180, 32));
        filterRow.add(txtCariFilm);

        JButton btnCari = cariBtn();
        filterRow.add(btnCari);

        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.setOpaque(false);
        north.add(title,     BorderLayout.NORTH);
        north.add(filterRow, BorderLayout.CENTER);

        // Tabel — kolom sesuai mockup: No, Film, Studio, Jam mulai, Jam selesai, Aksi
        modelJadwal = new DefaultTableModel(
            new Object[]{"No", "Film", "Studio", "Jam mulai", "Jam selesai", "Aksi"}, 0) {
            public boolean isCellEditable(int r, int c) { return c == 5; }
        };

        tableJadwal = styledTable(modelJadwal);

        // Renderer & editor tombol "Pilih" hijau di kolom Aksi
        tableJadwal.getColumn("Aksi").setCellRenderer(new PilihRenderer());
        tableJadwal.getColumn("Aksi").setCellEditor(
            new PilihEditor(new JCheckBox(), this::pilihKursi));
        tableJadwal.getColumn("No")  .setPreferredWidth(40);
        tableJadwal.getColumn("Aksi").setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(tableJadwal);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 180, 185), 1));
        scroll.getViewport().setBackground(ThemeColor.WHITE);

        p.add(north,  BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JButton dropdownBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(ThemeColor.fontBold(12));
        b.setForeground(ThemeColor.DARK_BROWN);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 32));
        return b;
    }

    private JButton cariBtn() {
        JButton b = new JButton("Cari") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeColor.SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(ThemeColor.fontBold(13));
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(80, 32));
        return b;
    }

    private void pilihKursi() {
        int row = tableJadwal.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Silakan klik baris jadwal yang ingin dipilih!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tableJadwal.convertRowIndexToModel(row);
        int    no    = Integer.parseInt(modelJadwal.getValueAt(modelRow, 0).toString());
        String judul = modelJadwal.getValueAt(modelRow, 1).toString();
        new PilihSeatFrame(currentUserId, no, 1, judul).setVisible(true);
        dispose();
    }

    private void setupSearch() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelJadwal);
        tableJadwal.setRowSorter(sorter);
        txtCariFilm.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String t = txtCariFilm.getText().trim();
                sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t, 1));
            }
        });
    }

    private void doLogout() {
        int ok = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) { new LoginFrame(); dispose(); }
    }

    // ─── Tabel styling — header coklat gelap, baris alternating ──────────────
    private JTable styledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(ThemeColor.fontPlain(13));
        tbl.setRowHeight(42);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(ThemeColor.ACCENT_LIGHT);
        tbl.setSelectionForeground(ThemeColor.DARK_BROWN);
        tbl.setBackground(ThemeColor.WHITE);

        JTableHeader header = tbl.getTableHeader();
        header.setBackground(ThemeColor.SIDEBAR_ACTIVE);
        header.setForeground(Color.WHITE);
        header.setFont(ThemeColor.fontBold(13));
        header.setPreferredSize(new Dimension(0, 42));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);

        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0
                        ? ThemeColor.WHITE
                        : new Color(250, 242, 244));
                    setForeground(new Color(60, 40, 35));
                }
                setHorizontalAlignment(col == 0 || col >= 3 ? CENTER : LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
        return tbl;
    }

    // ─── Tombol "Pilih" di kolom Aksi ────────────────────────────────────────
    static class PilihRenderer extends JButton implements TableCellRenderer {
        PilihRenderer() { setOpaque(true); setFont(ThemeColor.fontBold(12)); }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean focus, int row, int col) {
            setText("Pilih");
            setBackground(ThemeColor.GREEN_BTN);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
            return this;
        }
    }

    static class PilihEditor extends DefaultCellEditor {
        private final JButton btn;
        PilihEditor(JCheckBox cb, Runnable action) {
            super(cb);
            btn = new JButton("Pilih");
            btn.setBackground(ThemeColor.GREEN_BTN);
            btn.setForeground(Color.WHITE);
            btn.setFont(ThemeColor.fontBold(12));
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
            btn.addActionListener(e -> { fireEditingStopped(); action.run(); });
        }
        @Override public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int row, int col) { return btn; }
        @Override public Object getCellEditorValue() { return "Pilih"; }
    }
}