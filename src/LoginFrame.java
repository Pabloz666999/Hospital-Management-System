import components.ColorPalette;
import components.ModernButton;
import components.ModernPasswordField;
import components.ModernTextField;
import components.RoundedBorder;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    
    public LoginFrame() {
        setTitle("Ruang Sehat - Login");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);
        
        JPanel leftPanel = createLeftPanel();
        
        JPanel rightPanel = createRightPanel();
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, ColorPalette.PRIMARY, getWidth(), getHeight(), ColorPalette.SECONDARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.setColor(new Color(255, 255, 255, 40)); 
                g2.fillRoundRect(150, 150, 200, 250, 30, 30);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(250, 210, 250, 310);
                g2.drawLine(200, 260, 300, 260);
                
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(50, 50, 100, 100);
                g2.fillOval(350, 500, 150, 150);
            }
        };
        panel.setPreferredSize(new Dimension(500, 650));
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        panel.setBackground(ColorPalette.BACKGROUND);
        
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setPreferredSize(new Dimension(400, 520));
        
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(235, 235, 240), 1, 20), 
            BorderFactory.createEmptyBorder(40, 40, 40, 40) 
        ));
        
        JLabel iconLabel = new JLabel("🏥");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Ruang Sehat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Admin Login");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(iconLabel);
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(titleLabel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createVerticalStrut(40));
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usernameLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel userLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        userLabelPanel.setBackground(Color.WHITE);
        userLabelPanel.setMaximumSize(new Dimension(340, 20));
        userLabelPanel.add(usernameLabel);
        
        usernameField = new ModernTextField("Enter your username");
        usernameField.setMaximumSize(new Dimension(340, 45));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginCard.add(userLabelPanel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(usernameField);
        loginCard.add(Box.createVerticalStrut(20));
        
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        
        JPanel passLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passLabelPanel.setBackground(Color.WHITE);
        passLabelPanel.setMaximumSize(new Dimension(340, 20));
        passLabelPanel.add(passwordLabel);
        
        passwordField = new ModernPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(340, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginCard.add(passLabelPanel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(30));
        
        ModernButton loginButton = new ModernButton("Login", 
            ColorPalette.PRIMARY, 
            ColorPalette.PRIMARY_DARK);
        loginButton.setMaximumSize(new Dimension(340, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        
        passwordField.addActionListener(e -> handleLogin());
        
        loginCard.add(loginButton);
        
        panel.add(loginCard);
        return panel;
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.equals("admin") && password.equals("admin123")) {
            dispose();
            new MainMenuFrame();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password!", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}