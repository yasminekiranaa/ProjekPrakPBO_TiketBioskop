package view;

import database.Koneksi;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;


public class AdminDashboard extends JFrame {

    private Connection conn;

    // ── Film ──
    private JTable tblFilm;
    private DefaultTableModel modelFilm;
    private JTextField txtJudul, txtDurasi, txtGenre, txtCari;

    // ── Jadwal ──
    private JTable tblJadwal;
    private DefaultTableModel modelJadwal;
    private JComboBox<String> cbFilm, cbStudio;
    private JTextField txtTanggal, txtMulai;

    // ── Layout ──
    private JPanel contentArea;
    private CardLayout contentCard;

    public AdminDashboard() {
        try {
            this.conn = Koneksi.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal Koneksi Database: " + e.getMessage());
        }

        setTitle("Admin Panel - Bioskop Inces");
        setSize(1050, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(ThemeColor.LIGHT_GRAY);

        // ── Root layout ──────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeColor.LIGHT_GRAY);

        // Navbar
        NavbarPanel navbar = new NavbarPanel("Admin", this::doLogout);
        root.add(navbar, BorderLayout.NORTH);

        // Body = Sidebar + Content
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(ThemeColor.LIGHT_GRAY);

        // Sidebar
        String[][] menus = {
            {"film",   "🎬", "Kelola Film"},
            {"jadwal", "📅", "Kelola Jadwal"},
        };
        SidebarPanel sidebar = new SidebarPanel(menus, "film", key -> {
            contentCard.show(contentArea, key);
        });
        body.add(sidebar, BorderLayout.WEST);

        // Content area (CardLayout)
        contentCard = new CardLayout();
        contentArea = new JPanel(contentCard);
        contentArea.setBackground(ThemeColor.LIGHT_GRAY);
        contentArea.add(buildPanelFilm(),   "film");
        contentArea.add(buildPanelJadwal(), "jadwal");
        body.add(contentArea, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        add(root);

        // Load data awal
        loadFilm();
        loadJadwal();
        updateComboFilm();
    }

    // ─── Panel Kelola Film ────────────────────────────────────────────────────
    private JPanel buildPanelFilm() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(ThemeColor.LIGHT_GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Judul halaman
        JLabel judul = new JLabel("Kelola Film");
        judul.setFont(ThemeColor.fontBold(20));
        judul.setForeground(ThemeColor.DARK_BROWN);

        // ── Form Input ────────────────────────────────────────────────────────
        JPanel formCard = buildCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtJudul = styledInput("Judul Film");
        txtDurasi = styledInput("Durasi (menit)");
        txtGenre  = styledInput("Genre");

        RoundedButton btnSimpan = new RoundedButton("+ Simpan Film", ThemeColor.ACCENT, 8);
        RoundedButton btnHapus  = new RoundedButton("🗑 Hapus Terpilih", ThemeColor.RED, 8);
        btnSimpan.addActionListener(e -> simpanFilm());
        btnHapus.addActionListener(e -> hapusFilm());

        // Row 0: label
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.3;
        formCard.add(fieldLabel("Judul Film:"), gbc);
        gbc.gridx=1; gbc.weightx=0.7;
        formCard.add(txtJudul, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.3;
        formCard.add(fieldLabel("Durasi (Menit):"), gbc);
        gbc.gridx=1; gbc.weightx=0.7;
        formCard.add(txtDurasi, gbc);

        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.3;
        formCard.add(fieldLabel("Genre:"), gbc);
        gbc.gridx=1; gbc.weightx=0.7;
        formCard.add(txtGenre, gbc);

        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.5;
        formCard.add(btnSimpan, gbc);
        gbc.gridx=1; gbc.weightx=0.5;
        formCard.add(btnHapus, gbc);

        // ── Search ────────────────────────────────────────────────────────────
        JPanel searchCard = buildCard();
        searchCard.setLayout(new BorderLayout(10, 0));
        searchCard.setBorder(BorderFactory.createCompoundBorder(
            searchCard.getBorder(),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeColor.ACCENT_LIGHT, 1, true),
                "🔍 Pencarian",
                TitledBorder.LEFT, TitledBorder.TOP,
                ThemeColor.fontBold(12), ThemeColor.ACCENT
            )
        ));
        txtCari = styledInput("Ketik judul film...");
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { cariFilm(); }
        });
        searchCard.add(new JLabel("  Ketik Judul: "), BorderLayout.WEST);
        searchCard.add(txtCari, BorderLayout.CENTER);

        JPanel northWrap = new JPanel(new BorderLayout(0, 10));
        northWrap.setOpaque(false);
        northWrap.add(judul, BorderLayout.NORTH);
        northWrap.add(formCard, BorderLayout.CENTER);
        northWrap.add(searchCard, BorderLayout.SOUTH);

        // ── Tabel ─────────────────────────────────────────────────────────────
        modelFilm = new DefaultTableModel(new String[]{"ID Film", "Judul", "Durasi (mnt)", "Genre"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblFilm = styledTable(modelFilm);

        JScrollPane scroll = new JScrollPane(tblFilm);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeColor.ACCENT_LIGHT, 1));
        scroll.getViewport().setBackground(ThemeColor.WHITE);

        p.add(northWrap, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─── Panel Kelola Jadwal ──────────────────────────────────────────────────
    private JPanel buildPanelJadwal() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(ThemeColor.LIGHT_GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel judul = new JLabel("Kelola Jadwal");
        judul.setFont(ThemeColor.fontBold(20));
        judul.setForeground(ThemeColor.DARK_BROWN);

        // ── Form Jadwal ───────────────────────────────────────────────────────
        JPanel formCard = buildCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbFilm   = styledCombo(new String[]{});
        cbStudio = styledCombo(new String[]{"1", "2"});
        txtTanggal = styledInput("YYYY-MM-DD");
        txtMulai   = styledInput("HH:mm  (contoh: 19:00)");

        RoundedButton btnTerbitkan = new RoundedButton("📋 Terbitkan Jadwal", ThemeColor.BLUE, 8);
        RoundedButton btnHapusJ    = new RoundedButton("🗑 Hapus Terpilih",   ThemeColor.RED, 8);
        btnTerbitkan.addActionListener(e -> simpanJadwal());
        btnHapusJ.addActionListener(e -> hapusJadwal());

        Object[][] rows = {
            {"Pilih Film:",            cbFilm},
            {"Studio:",                cbStudio},
            {"Tanggal (YYYY-MM-DD):",  txtTanggal},
            {"Jam Mulai (HH:mm):",     txtMulai},
        };
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx=0; gbc.gridy=i; gbc.weightx=0.3;
            formCard.add(fieldLabel((String) rows[i][0]), gbc);
            gbc.gridx=1; gbc.weightx=0.7;
            formCard.add((Component) rows[i][1], gbc);
        }
        gbc.gridx=0; gbc.gridy=rows.length; gbc.weightx=0.5;
        formCard.add(btnTerbitkan, gbc);
        gbc.gridx=1; gbc.weightx=0.5;
        formCard.add(btnHapusJ, gbc);

        JPanel northWrap = new JPanel(new BorderLayout(0, 10));
        northWrap.setOpaque(false);
        northWrap.add(judul, BorderLayout.NORTH);
        northWrap.add(formCard, BorderLayout.CENTER);

        // ── Tabel ─────────────────────────────────────────────────────────────
        modelJadwal = new DefaultTableModel(
            new String[]{"ID Jadwal", "Film", "Studio", "Tanggal", "Mulai", "Selesai"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblJadwal = styledTable(modelJadwal);

        JScrollPane scroll = new JScrollPane(tblJadwal);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeColor.ACCENT_LIGHT, 1));
        scroll.getViewport().setBackground(ThemeColor.WHITE);

        p.add(northWrap, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─── LOGIKA FILM ─────────────────────────────────────────────────────────
    private void loadFilm() {
        modelFilm.setRowCount(0);
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM film");
            while (rs.next()) {
                modelFilm.addRow(new Object[]{
                    rs.getInt("id_film"),
                    rs.getString("judul"),
                    rs.getInt("durasi"),
                    rs.getString("genre")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cariFilm() {
        modelFilm.setRowCount(0);
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM film WHERE judul LIKE ?");
            ps.setString(1, "%" + txtCari.getText() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelFilm.addRow(new Object[]{
                    rs.getInt("id_film"), rs.getString("judul"),
                    rs.getInt("durasi"),  rs.getString("genre")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void simpanFilm() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO film (judul, durasi, genre) VALUES (?,?,?)");
            ps.setString(1, txtJudul.getText());
            ps.setInt(2, Integer.parseInt(txtDurasi.getText()));
            ps.setString(3, txtGenre.getText());
            ps.executeUpdate();
            loadFilm();
            updateComboFilm();
            txtJudul.setText(""); txtDurasi.setText(""); txtGenre.setText("");
            showSuccess("Film berhasil ditambahkan!");
        } catch (Exception e) {
            showError("Gagal Simpan: " + e.getMessage());
        }
    }

    private void hapusFilm() {
        int row = tblFilm.getSelectedRow();
        if (row == -1) { showWarning("Pilih film yang ingin dihapus!"); return; }
        try {
            String id = modelFilm.getValueAt(row, 0).toString();
            conn.createStatement().executeUpdate("DELETE FROM film WHERE id_film=" + id);
            loadFilm();
            updateComboFilm();
        } catch (SQLException e) {
            showError("Gagal Hapus! Film mungkin masih terikat data Jadwal.");
        }
    }

    // ─── LOGIKA JADWAL ───────────────────────────────────────────────────────
    private void loadJadwal() {
        modelJadwal.setRowCount(0);
        try {
            String sql = "SELECT j.id_jadwal, f.judul, j.id_studio, j.tanggal, j.jam_mulai, j.jam_selesai " +
                         "FROM jadwal j JOIN film f ON j.id_film = f.id_film";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                modelJadwal.addRow(new Object[]{
                    rs.getInt("id_jadwal"), rs.getString("judul"),
                    rs.getInt("id_studio"), rs.getString("tanggal"),
                    rs.getString("jam_mulai"), rs.getString("jam_selesai")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void simpanJadwal() {
        String tgl      = txtTanggal.getText().trim();
        String mulaiStr = txtMulai.getText().trim();
        int    std      = Integer.parseInt(cbStudio.getSelectedItem().toString());
        if (cbFilm.getItemCount() == 0) { showWarning("Belum ada film!"); return; }
        String judulTerpilih = cbFilm.getSelectedItem().toString();

        try {
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT id_film, durasi FROM film WHERE judul='" + judulTerpilih + "'");
            if (rs.next()) {
                int idF    = rs.getInt("id_film");
                int durasi = rs.getInt("durasi");
                java.time.LocalTime mulai   = java.time.LocalTime.parse(mulaiStr);
                java.time.LocalTime selesai = mulai.plusMinutes(durasi);
                String selesaiStr = selesai.toString();

                if (isBentrok(std, tgl, mulaiStr, selesaiStr)) {
                    showError("Studio " + std + " bentrok! Jadwal selesai jam " + selesaiStr);
                    return;
                }
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO jadwal (id_film, id_studio, tanggal, jam_mulai, jam_selesai) VALUES (?,?,?,?,?)");
                ps.setInt(1, idF); ps.setInt(2, std);
                ps.setString(3, tgl); ps.setString(4, mulaiStr); ps.setString(5, selesaiStr);
                ps.executeUpdate();
                loadJadwal();
                txtTanggal.setText(""); txtMulai.setText("");
                showSuccess("Jadwal berhasil diterbitkan!");
            }
        } catch (java.time.format.DateTimeParseException ex) {
            showError("Format jam salah! Gunakan HH:mm (Contoh: 19:00)");
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private boolean isBentrok(int std, String tgl, String mulai, String selesai) {
        try {
            String sql = "SELECT * FROM jadwal WHERE id_studio=? AND tanggal=? " +
                "AND ((jam_mulai < ? AND jam_selesai > ?) OR (jam_mulai < ? AND jam_selesai > ?))";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, std); ps.setString(2, tgl);
            ps.setString(3, selesai); ps.setString(4, mulai);
            ps.setString(5, selesai); ps.setString(6, mulai);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    private void hapusJadwal() {
        int row = tblJadwal.getSelectedRow();
        if (row == -1) { showWarning("Pilih jadwal yang ingin dihapus!"); return; }
        try {
            String id = modelJadwal.getValueAt(row, 0).toString();
            conn.createStatement().executeUpdate("DELETE FROM jadwal WHERE id_jadwal=" + id);
            loadJadwal();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateComboFilm() {
        cbFilm.removeAllItems();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT judul FROM film");
            while (rs.next()) cbFilm.addItem(rs.getString("judul"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame();
            this.dispose();
        }
    }

    // ─── Helper UI ────────────────────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setBackground(ThemeColor.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.ACCENT_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    private JTextField styledInput(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(ThemeColor.fontPlain(13));
        tf.setBackground(ThemeColor.LIGHT_GRAY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeColor.ACCENT_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(200, 34));
        return tf;
    }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(ThemeColor.fontPlain(13));
        cb.setBackground(ThemeColor.WHITE);
        cb.setPreferredSize(new Dimension(200, 34));
        return cb;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeColor.fontBold(13));
        lbl.setForeground(ThemeColor.DARK_BROWN);
        return lbl;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(ThemeColor.fontPlain(13));
        tbl.setRowHeight(36);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(ThemeColor.ACCENT_LIGHT);
        tbl.setSelectionForeground(ThemeColor.DARK_BROWN);
        tbl.setBackground(ThemeColor.WHITE);
        tbl.setForeground(new Color(50, 50, 50));

        // Header
        JTableHeader header = tbl.getTableHeader();
        header.setBackground(ThemeColor.DARK_BROWN);
        header.setForeground(ThemeColor.WHITE);
        header.setFont(ThemeColor.fontBold(13));
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Alternating row renderer
        tbl.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? ThemeColor.WHITE : ThemeColor.LIGHT_GRAY);
                    setForeground(new Color(50, 50, 50));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        return tbl;
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}