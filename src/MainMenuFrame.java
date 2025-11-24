import components.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {
    
    public MainMenuFrame() {
        setTitle("QSmart Hospital - Hospital Management System");
        setSize(1000, 650); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(ColorPalette.BACKGROUND);
        contentWrapper.setPreferredSize(new Dimension(900, 480)); 
        
        JPanel headerPanel = createHeader();
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0)); 
        cardsPanel.setBackground(ColorPalette.BACKGROUND);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        cardsPanel.add(createMenuCard(
            "📱", 
            "Patient Kiosk", 
            "Self-service registration & QR code generation"
        ));
        
        cardsPanel.add(createMenuCard(
            "🖥️", 
            "Display Board", 
            "Real-time queue status view for waiting room"
        ));
        
        cardsPanel.add(createMenuCard(
            "🎛️", 
            "Admin Dashboard", 
            "Complete queue management and analytics"
        ));
        
        contentWrapper.add(headerPanel);
        contentWrapper.add(Box.createVerticalStrut(30));
        contentWrapper.add(cardsPanel);
        
        mainPanel.add(contentWrapper);
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(ColorPalette.BACKGROUND);
        
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        titleRow.setBackground(ColorPalette.BACKGROUND);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 100, 230), 0, getHeight(), new Color(90, 70, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        logoIcon.setPreferredSize(new Dimension(45, 45));
        logoIcon.setLayout(new GridBagLayout());
        JLabel emojiLogo = new JLabel("🏥");
        emojiLogo.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        emojiLogo.setForeground(Color.WHITE);
        logoIcon.add(emojiLogo);
        
        JLabel titleLabel = new JLabel("QSmart Hospital");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 50, 120));
        
        titleRow.add(logoIcon);
        titleRow.add(titleLabel);
        
        JLabel instructionLabel = new JLabel("Select a component to view");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        container.add(titleRow);
        container.add(Box.createVerticalStrut(8));
        container.add(Box.createVerticalStrut(5));
        container.add(instructionLabel);
        
        return container;
    }
    
    private JPanel createMenuCard(String iconEmoji, String title, String description) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth() - 1;
                int h = getHeight() - 1;
                int arc = 25; 

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                if (isHovered) {
                    g2.setColor(ColorPalette.PRIMARY);
                    g2.setStroke(new BasicStroke(1.5f));
                } else {
                    g2.setColor(new Color(230, 230, 230));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(0, 0, w, h, arc, arc);
                g2.dispose();
            }
        };
        
        card.setLayout(new GridBagLayout());
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel contentInfo = new JPanel();
        contentInfo.setLayout(new BoxLayout(contentInfo, BoxLayout.Y_AXIS));
        contentInfo.setOpaque(false);
        
        JPanel iconBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(130, 110, 240), 0, getHeight(), new Color(90, 70, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        iconBg.setPreferredSize(new Dimension(70, 70)); 
        iconBg.setMaximumSize(new Dimension(70, 70)); // Penting agar tidak stretch di BoxLayout
        iconBg.setLayout(new GridBagLayout());
        iconBg.setAlignmentX(Component.CENTER_ALIGNMENT); // Center di BoxLayout
        
        JLabel iconLabel = new JLabel(iconEmoji);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 34));
        iconLabel.setForeground(Color.WHITE);
        iconBg.add(iconLabel);
        
        // 2. Labels
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(80, 60, 160));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setPreferredSize(new Dimension(180, 50));
        descLabel.setMaximumSize(new Dimension(200, 60)); 
        
        contentInfo.add(iconBg);
        contentInfo.add(Box.createVerticalStrut(25)); 
        contentInfo.add(titleLabel);
        contentInfo.add(Box.createVerticalStrut(10)); 
        contentInfo.add(descLabel);
        
        card.add(contentInfo);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (title.equals("Patient Kiosk")) {
                    dispose();
                    new PatientKioskFrame(); 
                } else if (title.equals("Display Board")) {
                    dispose();
                    new DisplayBoardFrame(); 
                } else {
                    JOptionPane.showMessageDialog(MainMenuFrame.this, 
                        "Opening " + title + "...\n(Feature will be implemented)", 
                        title, JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(260, 300)); 
        wrapper.add(card);
        
        return wrapper;
    }
}