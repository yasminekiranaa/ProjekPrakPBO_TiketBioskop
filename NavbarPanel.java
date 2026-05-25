package view;

import javax.swing.*;
import java.awt.*;

public class NavbarPanel extends JPanel {

    public NavbarPanel(String userLabel, Runnable logoutAction) {
        setLayout(new BorderLayout());
        setBackground(ThemeColor.PINK_BG);
        setPreferredSize(new Dimension(100, 64));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeColor.ACCENT_LIGHT));

        // ── Kiri: logo emoji + nama ──────────────────────────────────────────
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel lblEmoji = new JLabel("🎬🍿");
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel lblApp = new JLabel("Bioskop Inces");
        lblApp.setFont(ThemeColor.fontBold(18));
        lblApp.setForeground(ThemeColor.DARK_BROWN);

        left.add(lblEmoji);
        left.add(lblApp);

        // ── Kanan: user + logout ─────────────────────────────────────────────
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        // Icon user (pakai karakter unicode sederhana)
        JLabel lblUser = new JLabel("👤  " + userLabel);
        lblUser.setFont(ThemeColor.fontBold(14));
        lblUser.setForeground(ThemeColor.DARK_BROWN);

        JButton btnLogout = new JButton("⏻  LogOut");
        btnLogout.setFont(ThemeColor.fontBold(13));
        btnLogout.setForeground(ThemeColor.DARK_BROWN);
        btnLogout.setBackground(ThemeColor.PINK_BG);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logoutAction.run());
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogout.setForeground(ThemeColor.ACCENT);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogout.setForeground(ThemeColor.DARK_BROWN);
            }
        });

        right.add(lblUser);
        right.add(btnLogout);

        add(left,  BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }
}