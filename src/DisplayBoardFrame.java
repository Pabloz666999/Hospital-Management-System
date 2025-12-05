import db.QueueDao;
import db.AdminDao;
import scheduler.CounterHighlightScheduler;
import scheduler.QueueRefreshScheduler;
import components.ColorPalette;
import components.ModernButton;
import components.ModernTextField;
import components.ModernPasswordField;
import components.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayBoardFrame extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;

    private QueueDao.NowServingData nowServingData;
    private List<QueueDao.QueueItem> waitingQueue;
    private List<QueueDao.CounterItem> counterSummary;

    private JLabel nowServingNumberLabel;
    private JLabel nowServingPoliLabel;
    private JLabel nowServingLoketLabel;

    private JPanel waitingListContainer;
    private JPanel countersRow;
    private final List<JPanel> counterWrappers = new ArrayList<>();

    private QueueRefreshScheduler refreshScheduler;
    private CounterHighlightScheduler highlightScheduler;

    private CardLayout rootLayout;
    private JPanel rootPanel;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel mainMenuPanel;

    private ModernTextField loginUsernameField;
    private ModernPasswordField loginPasswordField;
    private final AdminDao adminDao = new AdminDao();

    public DisplayBoardFrame() {
        setTitle("Ruang Sehat - Layar Antrian");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        QueueDao queueDao = new QueueDao();
        nowServingData = queueDao.loadNowServing();
        waitingQueue = queueDao.loadWaitingQueue(5);
        counterSummary = queueDao.loadCounterSummary();

        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 30, 80),
                        getWidth(), getHeight(), new Color(70, 50, 140));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.75;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 30, 10, 10);
        contentPanel.add(createLeftSection(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.75;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 10, 10, 30);
        contentPanel.add(createRightSection(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;
        gbc.insets = new Insets(5, 50, 20, 50);
        contentPanel.add(createBottomSection(), gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);
        rootPanel.setOpaque(false);

        loginPanel = createLoginPanel();
        mainMenuPanel = createMainMenuPanel();

        rootPanel.add(mainPanel, "DISPLAY");
        rootPanel.add(loginPanel, "LOGIN");
        rootPanel.add(mainMenuPanel, "MENU");

        startClockThread();
        startDataRefresh();
        startHighlight();

        addEscToExit();

        add(rootPanel);
        rootLayout.show(rootPanel, "DISPLAY");
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        // Kiri: logo dan judul
        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftContainer.setOpaque(false);

        JLabel logoIcon = new JLabel("🏥");
        logoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Ruang Sehat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Status Antrian");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(200, 200, 255));

        textPanel.add(title);
        textPanel.add(subtitle);

        leftContainer.add(logoIcon);
        leftContainer.add(textPanel);

        // Kanan: jam + tombol login petugas
        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BoxLayout(rightContainer, BoxLayout.Y_AXIS));
        rightContainer.setOpaque(false);

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setOpaque(false);

        dateLabel = new JLabel("Sedang memuat...");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(220, 220, 220));
        dateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        timeLabel = new JLabel("00:00");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        timePanel.add(dateLabel);
        timePanel.add(timeLabel);

        JButton loginButton = new JButton("⏩  Login Petugas");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginButton.setForeground(new Color(109, 93, 222));
        loginButton.setBackground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        loginButton.setMargin(new Insets(6, 18, 6, 18));
        loginButton.addActionListener(e -> openStaffArea());

        rightContainer.add(timePanel);
        rightContainer.add(Box.createVerticalStrut(5));

        ModernButton staffLoginButton = new ModernButton(
                "Login Petugas",
                Color.WHITE,
                new Color(240, 240, 255));
        staffLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        staffLoginButton.setForeground(ColorPalette.PRIMARY);
        staffLoginButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        staffLoginButton.setPreferredSize(new Dimension(150, 32));
        staffLoginButton.setMaximumSize(new Dimension(160, 32));
        staffLoginButton.addActionListener(e -> openStaffArea());

        rightContainer.add(staffLoginButton);

        header.add(leftContainer, BorderLayout.WEST);
        header.add(rightContainer, BorderLayout.EAST);

        return header;
    }

    private void openStaffArea() {
        if (SessionManager.isAdminLoggedIn()) {
            showMainMenuPage();
        } else {
            showLoginPage();
        }
    }

    private JPanel createLoginPanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(ColorPalette.BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(840, 480));
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(235, 235, 240), 1, 22),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Login Petugas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Masuk untuk mengelola antrian");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(28));

        loginUsernameField = new ModernTextField("Enter your username");
        loginUsernameField.setMaximumSize(new Dimension(520, 44));

        loginPasswordField = new ModernPasswordField("Enter your password");
        loginPasswordField.setMaximumSize(new Dimension(520, 44));
        loginPasswordField.addActionListener(e -> handleInlineLogin());

        JPanel formColumn = new JPanel();
        formColumn.setLayout(new BoxLayout(formColumn, BoxLayout.Y_AXIS));
        formColumn.setOpaque(false);
        formColumn.setAlignmentX(Component.CENTER_ALIGNMENT);

        formColumn.add(createLoginField("Username", loginUsernameField));
        formColumn.add(Box.createVerticalStrut(18));
        formColumn.add(createLoginField("Password", loginPasswordField));
        formColumn.add(Box.createVerticalStrut(26));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonRow.setOpaque(false);

        JButton cancelButton = new JButton("Batal");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelButton.setForeground(ColorPalette.TEXT_SECONDARY);
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setOpaque(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        cancelButton.addActionListener(e -> showDisplayPage());

        ModernButton loginButton = new ModernButton("Login",
                ColorPalette.PRIMARY,
                ColorPalette.PRIMARY_DARK);
        loginButton.setPreferredSize(new Dimension(190, 42));
        loginButton.addActionListener(e -> handleInlineLogin());

        buttonRow.add(cancelButton);
        buttonRow.add(loginButton);

        formColumn.add(buttonRow);

        GridBagConstraints gch = new GridBagConstraints();
        gch.gridx = 0;
        gch.gridy = 0;
        gch.weightx = 1.0;
        gch.anchor = GridBagConstraints.NORTH;
        gch.insets = new Insets(0, 0, 10, 0);
        card.add(headerPanel, gch);

        GridBagConstraints gcf = new GridBagConstraints();
        gcf.gridx = 0;
        gcf.gridy = 1;
        gcf.weightx = 1.0;
        gcf.weighty = 1.0;
        gcf.anchor = GridBagConstraints.NORTH;
        card.add(formColumn, gcf);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        page.add(card, gbc);

        return page;
    }

    private JPanel createLoginField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(520, 90));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(ColorPalette.TEXT_PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        return panel;
    }

    private JPanel createMainMenuPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.setPreferredSize(new Dimension(900, 480));

        JPanel headerPanel = createMainMenuHeader();
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsPanel.setBackground(ColorPalette.BACKGROUND);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cardsPanel.add(createMainMenuCard(
                "📱",
                "Registrasi Pasien",
                "Pendaftaran layanan oleh petugas & pembuatan QR Code otomatis",
                this::showDisplayPage // sementara kembali ke layar antrian
        ));

        cardsPanel.add(createMainMenuCard(
                "📺",
                "Layar Antrian",
                "Pemantauan status antrian secara real-time di ruang tunggu",
                this::showDisplayPage));

        cardsPanel.add(createMainMenuCard(
                "📊",
                "Dashboard Admin",
                "Sistem manajemen antrian yang komprehensif dengan fitur analitik",
                () -> new AdminDashboardFrame()));

        contentWrapper.add(headerPanel);
        contentWrapper.add(Box.createVerticalStrut(30));
        contentWrapper.add(cardsPanel);

        main.add(contentWrapper);
        return main;
    }

    private JPanel createMainMenuHeader() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 100, 230), 0, getHeight(),
                        new Color(90, 70, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        logoIcon.setPreferredSize(new Dimension(45, 45));
        logoIcon.setLayout(new GridBagLayout());
        JLabel emojiLogo = new JLabel("📱");
        emojiLogo.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        emojiLogo.setForeground(Color.WHITE);
        logoIcon.add(emojiLogo);

        JLabel titleLabel = new JLabel("Ruang Sehat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 50, 120));

        titleRow.add(logoIcon);
        titleRow.add(titleLabel);

        JLabel instructionLabel = new JLabel("Silakan pilih fitur yang ingin Anda lihat");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(titleRow);
        container.add(Box.createVerticalStrut(8));
        container.add(instructionLabel);

        return container;
    }

    private JPanel createMainMenuCard(String iconEmoji, String title, String description, Runnable onClick) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(130, 110, 240), 0, getHeight(),
                        new Color(90, 70, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        iconBg.setPreferredSize(new Dimension(70, 70));
        iconBg.setMaximumSize(new Dimension(70, 70));
        iconBg.setLayout(new GridBagLayout());
        iconBg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(iconEmoji);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 34));
        iconLabel.setForeground(Color.WHITE);
        iconBg.add(iconLabel);

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
                if (onClick != null) {
                    onClick.run();
                }
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(260, 300));
        wrapper.add(card);

        return wrapper;
    }

    private void showLoginPage() {
        if (rootLayout != null && rootPanel != null) {
            rootLayout.show(rootPanel, "LOGIN");
        }
    }

    private void showDisplayPage() {
        if (rootLayout != null && rootPanel != null) {
            rootLayout.show(rootPanel, "DISPLAY");
        }
    }

    private void showMainMenuPage() {
        if (rootLayout != null && rootPanel != null) {
            rootLayout.show(rootPanel, "MENU");
        }
    }

    private void handleInlineLogin() {
        if (loginUsernameField == null || loginPasswordField == null) {
            return;
        }
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        if (username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Silakan isi username dan password.",
                    "Validasi Login",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = adminDao.authenticate(username.trim(), password);
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Username atau password salah.",
                    "Login Gagal",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SessionManager.setAdminLoggedIn(username.trim());
        showMainMenuPage();
    }

    private JPanel createLeftSection() {
        JPanel mainCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        mainCard.setLayout(new BorderLayout());
        mainCard.setOpaque(false);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel servingLabel = new JLabel("Nomor Antrian Dipanggil");
        servingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        servingLabel.setForeground(Color.GRAY);
        servingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel numberBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 100, 240), getWidth(), getHeight(),
                        new Color(90, 60, 180));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        numberBox.setMaximumSize(new Dimension(500, 220));
        numberBox.setLayout(new BoxLayout(numberBox, BoxLayout.Y_AXIS));
        numberBox.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        String ticketText = nowServingData != null && nowServingData.nomorAntrian != null
                ? nowServingData.nomorAntrian
                : "-";
        nowServingNumberLabel = new JLabel(ticketText);
        nowServingNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 90));
        nowServingNumberLabel.setForeground(Color.WHITE);
        nowServingNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String unitText = nowServingData != null && nowServingData.poliName != null
                ? nowServingData.poliName
                : "-";
        nowServingPoliLabel = new JLabel(unitText);
        nowServingPoliLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        nowServingPoliLabel.setForeground(new Color(220, 220, 255));
        nowServingPoliLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        numberBox.add(nowServingNumberLabel);
        numberBox.add(Box.createVerticalStrut(5));
        numberBox.add(nowServingPoliLabel);

        JPanel counterPanel = new JPanel();
        counterPanel.setLayout(new BoxLayout(counterPanel, BoxLayout.Y_AXIS));
        counterPanel.setOpaque(false);

        JLabel counterLabel = new JLabel("Silakan Menuju Loket");
        counterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        counterLabel.setForeground(Color.GRAY);
        counterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel counterBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(140, 120, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        counterBox.setPreferredSize(new Dimension(120, 70));
        counterBox.setMaximumSize(new Dimension(120, 70));
        counterBox.setLayout(new GridBagLayout());

        String counterText = nowServingData != null && nowServingData.loketName != null
                ? nowServingData.loketName.replace("Loket", "").trim()
                : "-";
        nowServingLoketLabel = new JLabel(counterText);
        nowServingLoketLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        nowServingLoketLabel.setForeground(Color.WHITE);
        counterBox.add(nowServingLoketLabel);

        counterPanel.add(counterLabel);
        counterPanel.add(Box.createVerticalStrut(5));
        counterPanel.add(counterBox);

        contentWrapper.add(servingLabel);
        contentWrapper.add(Box.createVerticalStrut(10));
        contentWrapper.add(numberBox);
        contentWrapper.add(Box.createVerticalStrut(20));
        contentWrapper.add(counterPanel);

        mainCard.add(contentWrapper, BorderLayout.CENTER);

        return mainCard;
    }

    private JPanel createRightSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel icon = new JLabel("⏳");
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel label = new JLabel("Daftar Antrian Berikutnya");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.WHITE);

        titlePanel.add(icon);
        titlePanel.add(Box.createHorizontalStrut(6));
        titlePanel.add(label);

        waitingListContainer = new JPanel();
        waitingListContainer.setLayout(new BoxLayout(waitingListContainer, BoxLayout.Y_AXIS));
        waitingListContainer.setOpaque(false);

        rebuildWaitingList();

        panel.add(titlePanel);
        panel.add(waitingListContainer);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createQueueItem(String index, String ticket, String dept) {
        JPanel item = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(1000, 60));
        item.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel circleWrapper = new JPanel(new GridBagLayout());
        circleWrapper.setOpaque(false);
        circleWrapper.setPreferredSize(new Dimension(40, 60));

        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(150, 130, 255));
                g2.fillOval(0, 0, 30, 30);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(index);
                int stringAscent = fm.getAscent();
                g2.drawString(index, (30 - stringWidth) / 2, (30 + stringAscent) / 2 - 2);
            }
        };
        circle.setPreferredSize(new Dimension(31, 31));
        circle.setOpaque(false);
        circleWrapper.add(circle);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel ticketLbl = new JLabel(ticket);
        ticketLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ticketLbl.setForeground(Color.WHITE);

        JLabel deptLbl = new JLabel(dept);
        deptLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deptLbl.setForeground(new Color(220, 220, 255));

        gbc.gridy = 0;
        textPanel.add(ticketLbl, gbc);
        gbc.gridy = 1;
        textPanel.add(deptLbl, gbc);

        item.add(circleWrapper, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);

        return item;
    }

    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        JLabel title = new JLabel("Ringkasan Loket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(230, 230, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        countersRow = new JPanel();
        countersRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        countersRow.setOpaque(false);

        rebuildCountersRow(bottomPanel);

        bottomPanel.add(title);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(countersRow);
        bottomPanel.add(Box.createVerticalGlue());

        return bottomPanel;
    }

    private JPanel createCounterBox(String counterNum, String ticketNum) {
        JPanel innerBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 70, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        innerBox.setOpaque(false);
        innerBox.setPreferredSize(new Dimension(80, 100));
        innerBox.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel counterLbl = new JLabel("LOKET " + counterNum);
        counterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        counterLbl.setForeground(new Color(180, 180, 255));
        counterLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ticketLbl = new JLabel(ticketNum);
        ticketLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        ticketLbl.setForeground(Color.WHITE);
        ticketLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(counterLbl);
        content.add(Box.createVerticalStrut(5));
        content.add(ticketLbl);

        innerBox.add(content);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(innerBox);
        wrapper.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        counterWrappers.add(wrapper);

        return wrapper;
    }

    private void startClockThread() {
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }

    private void startDataRefresh() {
        refreshScheduler = new QueueRefreshScheduler(() -> {
            QueueDao dao = new QueueDao();
            QueueDao.NowServingData ns = dao.loadNowServing();
            List<QueueDao.QueueItem> waiting = dao.loadWaitingQueue(5);
            List<QueueDao.CounterItem> counters = dao.loadCounterSummary();

            SwingUtilities.invokeLater(() -> {
                nowServingData = ns;
                waitingQueue = waiting;
                counterSummary = counters;
                updateNowServing();
                rebuildWaitingList();
                rebuildCountersRow(null);
            });
        });
        refreshScheduler.start(0, 5000);
    }

    private void startHighlight() {
        highlightScheduler = new CounterHighlightScheduler(
                counterWrappers.size(),
                5000,
                activeIndex -> SwingUtilities.invokeLater(() -> updateCounterHighlight(activeIndex)));
        highlightScheduler.start();
    }

    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        Date now = new Date();
        if (timeLabel != null)
            timeLabel.setText(timeFormat.format(now));
        if (dateLabel != null)
            dateLabel.setText(dateFormat.format(now));
    }

    private void updateNowServing() {
        String ticketText = nowServingData != null && nowServingData.nomorAntrian != null
                ? nowServingData.nomorAntrian
                : "-";
        String poliText = nowServingData != null && nowServingData.poliName != null
                ? nowServingData.poliName
                : "-";
        String loketText = nowServingData != null && nowServingData.loketName != null
                ? nowServingData.loketName.replace("Loket", "").trim()
                : "-";

        if (nowServingNumberLabel != null) {
            nowServingNumberLabel.setText(ticketText);
        }
        if (nowServingPoliLabel != null) {
            nowServingPoliLabel.setText(poliText);
        }
        if (nowServingLoketLabel != null) {
            nowServingLoketLabel.setText(loketText);
        }
    }

    private void rebuildWaitingList() {
        if (waitingListContainer == null) {
            return;
        }
        waitingListContainer.removeAll();
        waitingListContainer.add(Box.createVerticalStrut(15));

        if (waitingQueue != null && !waitingQueue.isEmpty()) {
            int index = 1;
            for (QueueDao.QueueItem item : waitingQueue) {
                waitingListContainer.add(createQueueItem(
                        String.valueOf(index++),
                        item.nomorAntrian,
                        item.poliName != null ? item.poliName : ""));
                waitingListContainer.add(Box.createVerticalStrut(8));
            }
        } else {
            JLabel empty = new JLabel("Belum ada antrian menunggu.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            empty.setForeground(new Color(230, 230, 255));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            waitingListContainer.add(empty);
        }

        waitingListContainer.revalidate();
        waitingListContainer.repaint();
    }

    private void rebuildCountersRow(JPanel bottomPanel) {
        if (countersRow == null) {
            return;
        }
        countersRow.removeAll();
        counterWrappers.clear();

        if (counterSummary != null && !counterSummary.isEmpty()) {
            int index = 1;
            for (QueueDao.CounterItem item : counterSummary) {
                String loketLabel = String.valueOf(index++);
                String ticket = item.nomorAntrian != null ? item.nomorAntrian : "-";
                countersRow.add(createCounterBox(loketLabel, ticket));
            }
        } else if (bottomPanel != null) {
            JLabel empty = new JLabel("Belum ada loket aktif.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            empty.setForeground(new Color(230, 230, 255));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            bottomPanel.add(empty);
        }

        countersRow.revalidate();
        countersRow.repaint();
    }

    private void updateCounterHighlight(int activeIndex) {
        if (counterWrappers.isEmpty()) {
            return;
        }
        for (int i = 0; i < counterWrappers.size(); i++) {
            JPanel wrapper = counterWrappers.get(i);
            if (i == activeIndex) {
                wrapper.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            } else {
                wrapper.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        }
    }

    private void addEscToExit() {
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "EXIT_APP");
        actionMap.put("EXIT_APP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayBoardFrame());
    }
}
