import components.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {
    
    public MainMenuFrame() {
        setTitle("Ruang Sehat - Menu Utama");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ColorPalette.BACKGROUND);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(ColorPalette.BACKGROUND);
        
        JPanel headerPanel = createHeader();
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 50, 0)); 
        cardsPanel.setBackground(ColorPalette.BACKGROUND);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        cardsPanel.add(createMenuCard(
            "📝", 
            "Registrasi Pasien", 
            "Pendaftaran layanan oleh petugas di loket pendaftaran"
        ));
        
        cardsPanel.add(createMenuCard(
            "🖥️", 
            "Layar Antrian", 
            "Pemantauan status antrian secara real-time di ruang tunggu"
        ));
        
        cardsPanel.add(createMenuCard(
            "📊", 
            "Dashboard Admin", 
            "Sistem manajemen antrian yang komprehensif dengan fitur analitik"
        ));
        
        contentWrapper.add(headerPanel);
        contentWrapper.add(Box.createVerticalStrut(50)); 
        contentWrapper.add(cardsPanel);
        
        mainPanel.add(contentWrapper);
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(ColorPalette.BACKGROUND);
        
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        titleRow.setBackground(ColorPalette.BACKGROUND);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ColorPalette.BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 100, 230), 0, getHeight(), new Color(90, 70, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        logoIcon.setPreferredSize(new Dimension(60, 60));
        logoIcon.setLayout(new GridBagLayout());
        
        JLabel emojiLogo = new JLabel("🏥");
        emojiLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        emojiLogo.setForeground(Color.WHITE);
        logoIcon.add(emojiLogo);
        
        JLabel titleLabel = new JLabel("Ruang Sehat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(new Color(60, 50, 120));
        
        titleRow.add(logoIcon);
        titleRow.add(titleLabel);
        
        JLabel instructionLabel = new JLabel("Silakan pilih fitur yang ingin Anda lihat");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        container.add(titleRow);
        container.add(Box.createVerticalStrut(15));
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
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int arc = 40; 

                g2.setColor(ColorPalette.BACKGROUND);
                g2.fillRect(0, 0, w, h);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

                if (isHovered) {
                    g2.setColor(ColorPalette.PRIMARY);
                    g2.setStroke(new BasicStroke(3f));
                } else {
                    g2.setColor(new Color(230, 230, 230));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                g2.dispose();
            }
        };
        
        card.setLayout(new GridBagLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel contentInfo = new JPanel();
        contentInfo.setLayout(new BoxLayout(contentInfo, BoxLayout.Y_AXIS));
        contentInfo.setOpaque(false);
        
        JPanel iconBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint gp = new GradientPaint(0, 0, new Color(130, 110, 240), 0, getHeight(), new Color(90, 70, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        iconBg.setPreferredSize(new Dimension(100, 100));
        iconBg.setMaximumSize(new Dimension(100, 100));
        iconBg.setLayout(new GridBagLayout());
        iconBg.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel(iconEmoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setForeground(Color.WHITE);
        iconBg.add(iconLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(80, 60, 160));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setPreferredSize(new Dimension(240, 70));
        descLabel.setMaximumSize(new Dimension(260, 80));
        
        contentInfo.add(iconBg);
        contentInfo.add(Box.createVerticalStrut(35));
        contentInfo.add(titleLabel);
        contentInfo.add(Box.createVerticalStrut(15));
        contentInfo.add(descLabel);
        
        card.add(contentInfo);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (title.equals("Registrasi Pasien")) {
                    dispose();
                    new PatientRegistration();
                } else if (title.equals("Layar Antrian")) {
                    dispose();
                    new DisplayBoardFrame();
                } else if (title.equals("Dashboard Admin")) {
                    dispose();
                    new AdminDashboardFrame();
                }
            }
        });
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorPalette.BACKGROUND); 
        wrapper.setPreferredSize(new Dimension(340, 420));
        wrapper.add(card);
        
        return wrapper;
    }
}