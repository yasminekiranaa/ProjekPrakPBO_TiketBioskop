package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class RoundedButton extends JButton {

    private final Color baseColor;
    private final int   radius;
    private Color       currentColor;

    public RoundedButton(String text, Color color, int radius) {
        super(text);
        this.baseColor    = color;
        this.currentColor = color;
        this.radius       = radius;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(ThemeColor.fontBold(13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                currentColor = baseColor.darker();
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                currentColor = baseColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}