package view;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;


public class SidebarPanel extends JPanel {

    private String activeKey;
    private final Map<String, JButton> btnMap = new LinkedHashMap<>();

    public SidebarPanel(String[][] menus, String selected, Consumer<String> callback) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ThemeColor.DARK_BROWN);
        setPreferredSize(new Dimension(200, 0));

        add(Box.createVerticalStrut(24));

        activeKey = selected;

        for (String[] menu : menus) {
            String key   = menu[0];
            String emoji = menu[1];
            String label = menu[2];

            JButton btn = buildBtn(emoji, label, key.equals(selected));
            btn.addActionListener(e -> {
                setActive(key);
                callback.accept(key);
            });
            btnMap.put(key, btn);

            // Wrapper dengan margin horizontal agar highlight tidak keluar sidebar
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            wrap.add(btn, BorderLayout.CENTER);
            wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            add(wrap);
            add(Box.createVerticalStrut(6));
        }

        add(Box.createVerticalGlue());
    }

    private JButton buildBtn(String emoji, String label, boolean active) {
        JButton btn = new JButton(emoji + "  " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                // Gambar background rounded rectangle jika aktif atau hover
                if (isSelected() || getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeColor.SIDEBAR_ACTIVE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setFont(ThemeColor.fontBold(14));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);   // ← TIDAK ADA kotak border
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 10));
        btn.setPreferredSize(new Dimension(0, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setSelected(active);
        return btn;
    }

    public void setActive(String key) {
        activeKey = key;
        btnMap.forEach((k, b) -> b.setSelected(k.equals(key)));
        repaint();
    }
}