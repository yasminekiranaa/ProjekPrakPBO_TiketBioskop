package view;

import controller.BookingController;
import model.Kursi;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class PilihSeatFrame extends JFrame {

    private BookingController bookingController = new BookingController();
    private List<Kursi> selectedSeats = new ArrayList<>();
    private int userId, jadwalId, studioId;
    private String judulFilm;

    public PilihSeatFrame(int userId, int jadwalId, int studioId, String judulFilm) {
        this.userId    = userId;
        this.jadwalId  = jadwalId;
        this.studioId  = studioId;
        this.judulFilm = judulFilm;

        setTitle("Pilih Kursi - " + judulFilm);
        setSize(900, 640);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeColor.PINK_LIGHT);

        // Navbar
        NavbarPanel navbar = new NavbarPanel("User", () -> {
            new UserMainPage(userId).setVisible(true);
            this.dispose();
        });
        root.add(navbar, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(ThemeColor.PINK_LIGHT);

        // Sidebar
        String[][] menus = {
            {"dashboard", "🕐", "Dashboard"},
            {"tiket",     "🎫", "Tiket Saya"},
            {"jadwal",    "📅", "Jadwal"},
        };
        SidebarPanel sidebar = new SidebarPanel(menus, "jadwal", k -> {});
        body.add(sidebar, BorderLayout.WEST);

        body.add(buildContent(), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ─── Konten Utama ─────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(ThemeColor.PINK_LIGHT);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Judul
        JLabel title = new JLabel("Pilih Kursi");
        title.setFont(ThemeColor.fontBold(20));
        title.setForeground(ThemeColor.DARK_BROWN);

        // Info bar
        JPanel infoBar = buildInfoBar();

        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(title,   BorderLayout.NORTH);
        north.add(infoBar, BorderLayout.CENTER);

        // Label Layar
        JLabel lblScreen = new JLabel("─────────── Layar Bioskop ───────────", SwingConstants.CENTER);
        lblScreen.setFont(ThemeColor.fontBold(12));
        lblScreen.setForeground(Color.WHITE);
        lblScreen.setOpaque(true);
        lblScreen.setBackground(new Color(74, 34, 23, 200));
        lblScreen.setPreferredSize(new Dimension(0, 32));
        lblScreen.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        // Grid kursi
        JPanel seatGrid = new JPanel();
        seatGrid.setBackground(ThemeColor.DARK_BROWN);
        seatGrid.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        List<Kursi> allSeats = bookingController.getDaftarKursi(studioId, jadwalId);
        int cols = 8;
        seatGrid.setLayout(new GridLayout(0, cols, 10, 10));

        for (Kursi k : allSeats) {
            seatGrid.add(buildSeatButton(k));
        }

        JPanel seatWrapper = new JPanel(new BorderLayout(0, 0));
        seatWrapper.setOpaque(false);

        // Wrapper coklat gelap untuk grid seat
        JPanel darkBox = new JPanel(new BorderLayout());
        darkBox.setBackground(ThemeColor.DARK_BROWN);
        darkBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 80, 60), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        darkBox.add(lblScreen, BorderLayout.NORTH);
        darkBox.add(new JScrollPane(seatGrid) {{
            setBorder(BorderFactory.createEmptyBorder());
            getViewport().setBackground(ThemeColor.DARK_BROWN);
        }}, BorderLayout.CENTER);

        seatWrapper.add(darkBox, BorderLayout.CENTER);

        // Legenda + tombol Pesan
        JPanel south = buildSouthPanel();

        p.add(north,       BorderLayout.NORTH);
        p.add(seatWrapper, BorderLayout.CENTER);
        p.add(south,       BorderLayout.SOUTH);

        return p;
    }

    // ─── Info Bar ─────────────────────────────────────────────────────────────
    private JPanel buildInfoBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 0, 0));
        bar.setBackground(ThemeColor.DARK_BROWN);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        String[][] cols = {
            {"Film",    judulFilm},
            {"Studio",  "Studio " + studioId},
            {"Tanggal", "12 mei 2026"},
            {"Jam",     "19:30"},
        };

        for (String[] col : cols) {
            JPanel cell = new JPanel();
            cell.setOpaque(false);
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

            JLabel lblKey = new JLabel(col[0]);
            lblKey.setFont(ThemeColor.fontPlain(11));
            lblKey.setForeground(new Color(200, 170, 170));
            lblKey.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblVal = new JLabel(col[1]);
            lblVal.setFont(ThemeColor.fontBold(13));
            lblVal.setForeground(Color.WHITE);
            lblVal.setAlignmentX(Component.CENTER_ALIGNMENT);

            cell.add(lblKey);
            cell.add(Box.createVerticalStrut(3));
            cell.add(lblVal);

            bar.add(cell);
        }
        return bar;
    }

    // ─── Tombol Kursi ─────────────────────────────────────────────────────────
    private JButton buildSeatButton(Kursi k) {
        // Warna berdasarkan tipe & status
        final Color colorAvail = k.getTipe().equalsIgnoreCase("VIP")
            ? ThemeColor.SEAT_VIP          // pink muda
            : ThemeColor.SEAT_AVAILABLE;   // hijau

        JButton btn = new JButton(k.getNamaKursi()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(50, 50));
        btn.setFont(ThemeColor.fontBold(10));
        btn.setForeground(new Color(40, 30, 25));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if ("BOOKED".equals(k.getStatus()) || "HOLD".equals(k.getStatus())) {
            btn.setBackground(ThemeColor.SEAT_SOLD);   // merah
            btn.setForeground(Color.WHITE);
            btn.setEnabled(false);
        } else {
            btn.setBackground(colorAvail);
        }

        btn.addActionListener(e -> {
            Color cur = btn.getBackground();
            if (cur.equals(colorAvail)) {
                btn.setBackground(ThemeColor.SEAT_SELECTED);  // kuning
                selectedSeats.add(k);
            } else if (cur.equals(ThemeColor.SEAT_SELECTED)) {
                btn.setBackground(colorAvail);
                selectedSeats.remove(k);
            }
            btn.repaint();
        });

        return btn;
    }

    // ─── Panel Bawah (Legenda + Tombol) ──────────────────────────────────────
    private JPanel buildSouthPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        // Legenda
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        legend.setOpaque(false);
        legend.add(legendItem(ThemeColor.SEAT_AVAILABLE, "Tersedia (reguler)"));
        legend.add(legendItem(ThemeColor.SEAT_VIP,       "Tersedia (VIP)"));
        legend.add(legendItem(ThemeColor.SEAT_SELECTED,  "Terpilih"));
        legend.add(legendItem(ThemeColor.SEAT_SOLD,      "Terjual"));

        // Tombol Pesan
        RoundedButton btnPesan = new RoundedButton("Pesan", ThemeColor.SIDEBAR_ACTIVE, 8);
        btnPesan.setFont(ThemeColor.fontBold(14));
        btnPesan.setPreferredSize(new Dimension(120, 42));
        btnPesan.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih minimal satu kursi!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                new PembayaranFrame(userId, jadwalId, studioId, selectedSeats, judulFilm).setVisible(true);
                this.dispose();
            }
        });

        p.add(legend,   BorderLayout.WEST);
        p.add(btnPesan, BorderLayout.EAST);
        return p;
    }

    // ─── Helper Legenda ──────────────────────────────────────────────────────
    private JPanel legendItem(Color c, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        item.setOpaque(false);

        JPanel box = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setPreferredSize(new Dimension(18, 18));

        JLabel lbl = new JLabel(text);
        lbl.setFont(ThemeColor.fontPlain(12));
        lbl.setForeground(ThemeColor.DARK_BROWN);

        item.add(box);
        item.add(lbl);
        return item;
    }
}