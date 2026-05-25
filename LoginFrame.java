package view;

import controller.AdminController;
import dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AdminController adminController = new AdminController();
    private final UserDAO         userDAO         = new UserDAO();

    private JTextField     txtAdminUser;
    private JPasswordField txtAdminPass;
    private JTextField     txtUserName;
    private JTextField     txtUserEmail;

    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private JButton    btnTabAdmin, btnTabUser;

    public LoginFrame() {
        setTitle("Bioskop Inces - Login");
        setSize(720, 460);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildLeft(),  BorderLayout.WEST);
        root.add(buildRight(), BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    // ═══════════════════════════════════════════════════════
    //  PANEL KIRI — pink + logo
    // ═══════════════════════════════════════════════════════
    private JPanel buildLeft() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillOval(-90, -90, 300, 300);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(60, 180, 260, 260);
            }
        };
        p.setBackground(ThemeColor.PINK_BG);
        p.setPreferredSize(new Dimension(310, 0));
        p.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel lblEmoji = new JLabel("🎬🍿");
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNama = new JLabel("Bioskop Inces");
        lblNama.setFont(ThemeColor.fontBold(22));
        lblNama.setForeground(ThemeColor.DARK_BROWN);
        lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Nonton film favorit kamu");
        lblSub.setFont(ThemeColor.fontPlain(13));
        lblSub.setForeground(ThemeColor.MED_BROWN);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(lblEmoji);
        inner.add(Box.createVerticalStrut(10));
        inner.add(lblNama);
        inner.add(Box.createVerticalStrut(5));
        inner.add(lblSub);

        p.add(inner);
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  PANEL KANAN — form putih + tab
    // ═══════════════════════════════════════════════════════
    private JPanel buildRight() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ThemeColor.WHITE);

        JPanel tabRow = new JPanel(new GridLayout(1, 2));
        tabRow.setPreferredSize(new Dimension(0, 46));
        tabRow.setBackground(ThemeColor.WHITE);

        btnTabAdmin = makeTabBtn("Admin Login", true);
        btnTabUser  = makeTabBtn("User Login",  false);
        btnTabAdmin.addActionListener(e -> switchTab("admin"));
        btnTabUser .addActionListener(e -> switchTab("user"));
        tabRow.add(btnTabAdmin);
        tabRow.add(btnTabUser);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(ThemeColor.WHITE);
        cardPanel.add(buildAdminForm(), "admin");
        cardPanel.add(buildUserForm(),  "user");

        p.add(tabRow,    BorderLayout.NORTH);
        p.add(cardPanel, BorderLayout.CENTER);
        return p;
    }

    private JButton makeTabBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(ThemeColor.fontBold(13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        styleTab(b, active);
        return b;
    }

    private void styleTab(JButton b, boolean active) {
        if (active) {
            b.setBackground(ThemeColor.WHITE);
            b.setForeground(ThemeColor.DARK_BROWN);
            b.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ThemeColor.DARK_BROWN));
        } else {
            b.setBackground(new Color(240, 240, 240));
            b.setForeground(new Color(170, 170, 170));
            b.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(215, 215, 215)));
        }
    }

    private void switchTab(String tab) {
        cardLayout.show(cardPanel, tab);
        styleTab(btnTabAdmin, "admin".equals(tab));
        styleTab(btnTabUser,  "user" .equals(tab));
    }

    // ═══════════════════════════════════════════════════════
    //  Form Admin
    // ═══════════════════════════════════════════════════════
    private JPanel buildAdminForm() {
        JPanel p = wrap();

        JLabel heading = new JLabel("Admin Login");
        heading.setFont(ThemeColor.fontBold(20));
        heading.setForeground(ThemeColor.DARK_BROWN);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtAdminUser = field();
        txtAdminPass = passField();

        p.add(heading);
        p.add(Box.createVerticalStrut(18));
        p.add(fldLbl("Username"));
        p.add(Box.createVerticalStrut(5));
        p.add(txtAdminUser);
        p.add(Box.createVerticalStrut(14));
        p.add(fldLbl("Password"));
        p.add(Box.createVerticalStrut(5));
        p.add(txtAdminPass);
        p.add(Box.createVerticalStrut(22));
        p.add(loginBtn(this::doAdminLogin));
        p.add(Box.createVerticalStrut(12));
        p.add(switchLnk("Login sebagai User", () -> switchTab("user")));
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  Form User
    // ═══════════════════════════════════════════════════════
    private JPanel buildUserForm() {
        JPanel p = wrap();

        JLabel heading = new JLabel("User Login");
        heading.setFont(ThemeColor.fontBold(20));
        heading.setForeground(ThemeColor.DARK_BROWN);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUserName  = field();
        txtUserEmail = field();

        p.add(heading);
        p.add(Box.createVerticalStrut(18));
        p.add(fldLbl("Nama"));
        p.add(Box.createVerticalStrut(5));
        p.add(txtUserName);
        p.add(Box.createVerticalStrut(14));
        p.add(fldLbl("Email"));
        p.add(Box.createVerticalStrut(5));
        p.add(txtUserEmail);
        p.add(Box.createVerticalStrut(22));
        p.add(loginBtn(this::doUserLogin));
        p.add(Box.createVerticalStrut(12));
        p.add(switchLnk("Login sebagai Admin", () -> switchTab("admin")));
        return p;
    }

    // ═══════════════════════════════════════════════════════
    //  Aksi Login
    // ═══════════════════════════════════════════════════════
    private void doAdminLogin() {
        String user = txtAdminUser.getText().trim();
        String pass = new String(txtAdminPass.getPassword());

        // [PERBAIKAN] Validasi field tidak boleh kosong sebelum query
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan Password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (adminController.login(user, pass)) {
            JOptionPane.showMessageDialog(this, "Login Admin Berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            new AdminDashboard().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            // [PERBAIKAN] Kosongkan field password setelah gagal login (keamanan)
            txtAdminPass.setText("");
        }
    }

    private void doUserLogin() {
        String nama  = txtUserName.getText().trim();
        String email = txtUserEmail.getText().trim();

        // [PERBAIKAN] Validasi field tidak boleh kosong
        if (nama.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nama dan Email tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // [PERBAIKAN] Validasi format email dengan regex sederhana
        if (!email.matches("^[\\w.+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                "Format email tidak valid!\nContoh: nama@email.com", "Email Tidak Valid", JOptionPane.WARNING_MESSAGE);
            txtUserEmail.requestFocus();
            return;
        }

        // [PERBAIKAN] Validasi panjang nama minimal 2 karakter
        if (nama.length() < 2) {
            JOptionPane.showMessageDialog(this,
                "Nama terlalu pendek, minimal 2 karakter!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtUserName.requestFocus();
            return;
        }

        int userId = userDAO.loginOrRegister(nama, email);
        if (userId != -1) {
            JOptionPane.showMessageDialog(this, "Selamat Datang, " + nama + "!");
            new UserMainPage(userId).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal login, coba lagi.\nPastikan koneksi database aktif.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  UI Helpers
    // ═══════════════════════════════════════════════════════
    private JPanel wrap() {
        JPanel p = new JPanel();
        p.setBackground(ThemeColor.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(24, 48, 24, 48));
        return p;
    }

    private JLabel fldLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(ThemeColor.fontPlain(12));
        l.setForeground(ThemeColor.DARK_BROWN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField field() {
        JTextField tf = new JTextField();
        tf.setFont(ThemeColor.fontPlain(13));
        tf.setBackground(ThemeColor.WHITE);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return tf;
    }

    private JPasswordField passField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(ThemeColor.fontPlain(13));
        pf.setBackground(ThemeColor.WHITE);
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return pf;
    }

    private JButton loginBtn(Runnable action) {
        JButton btn = new JButton("LOGIN") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 170, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(ThemeColor.fontBold(14));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JLabel switchLnk(String text, Runnable action) {
        JLabel lbl = new JLabel("<html><center><u>" + text + "</u></center></html>");
        lbl.setFont(ThemeColor.fontPlain(12));
        lbl.setForeground(new Color(100, 160, 220));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
        });
        return lbl;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}