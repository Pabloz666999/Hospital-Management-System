package components;

import javax.swing.*;
import java.awt.*;

public class ModernPasswordField extends JPasswordField {
    private String placeholder;

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder != null && !placeholder.isEmpty()
                && getPassword().length == 0 && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ColorPalette.TEXT_SECONDARY);
            g2.setFont(getFont());

            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int x = insets.left + 5;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}
