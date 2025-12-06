import components.ColorPalette;
import components.ModernButton;
import components.ModernPasswordField;
import components.ModernTextField;
import components.RoundedBorder;
import db.AdminDao;
import model.Admin;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    
    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    private final AdminDao adminDao = new AdminDao();

    public LoginFrame() {
        setTitle("Ruang Sehat - Login Petugas");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Utama
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);

        // Kartu Login
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setPreferredSize(new Dimension(500, 450)); 
        loginCard.setMaximumSize(new Dimension(500, 450));
        
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(230, 230, 235), 1, 25),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        // --- KONTEN ---
        JLabel titleLabel = new JLabel("Login Petugas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Masuk untuk mengelola antrian");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new ModernTextField("Masukkan username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        passwordField = new ModernPasswordField("Masukkan password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.addActionListener(e -> handleLogin()); 

        ModernButton loginButton = new ModernButton("Login", ColorPalette.PRIMARY, ColorPalette.PRIMARY_DARK);
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.addActionListener(e -> handleLogin());

        JButton cancelButton = new JButton("Batal");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelButton.setForeground(ColorPalette.TEXT_SECONDARY);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            dispose();
            new DisplayBoardFrame();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(titleLabel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createVerticalStrut(35));
        
        loginCard.add(createLabel("Username"));
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(usernameField);
        
        loginCard.add(Box.createVerticalStrut(15));
        
        loginCard.add(createLabel("Password"));
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(passwordField);
        
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(buttonPanel);

        mainPanel.add(loginCard);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLabel(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(ColorPalette.TEXT_PRIMARY);
        p.add(l);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return p;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan isi username dan password.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Admin admin = adminDao.findByCredentials(username.trim(), password);
        
        if (admin != null) {
            SessionManager.setAdminLoggedIn(admin);
            dispose();
            new MainMenuFrame();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}