import components.ColorPalette;
import components.ModernButton;
import components.RoundedBorder;
import db.DashboardStatsDao;
import db.LoketDao;
import db.QueueDao;
import scheduler.QueueRefreshScheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboardFrame extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton btnAnalytics, btnQueue;

    private JPanel analyticsCard;
    private JPanel queueCard;
    private String currentCardName = "ANALYTICS";

    private JTable queueTable;
    private QueueRefreshScheduler refreshScheduler;
    private JComboBox<LoketItem> loketCombo;

    // Label nilai pada kartu statistik
    private JLabel lblTotalPatients;
    private JLabel lblAvgWait;
    private JLabel lblActiveCounters;
    private JLabel lblValidQueues;

    // Callback ketika tombol back di header ditekan
    private final Runnable onBack;

    public AdminDashboardFrame() {
        this(null);
    }

    public AdminDashboardFrame(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(ColorPalette.BACKGROUND);

        // Inisialisasi label statistik dengan nilai default
        lblTotalPatients = new JLabel("0");
        lblAvgWait = new JLabel("0 mnt");
        lblActiveCounters = new JLabel("0");
        lblValidQueues = new JLabel("0");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorPalette.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        // --- ATAS: Statistik + Navigasi ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(ColorPalette.BACKGROUND);

        topContainer.add(createStatsPanel());
        topContainer.add(Box.createVerticalStrut(20));
        topContainer.add(createNavigationMenu()); // MENU NAVIGASI BARU
        topContainer.add(Box.createVerticalStrut(15));

        // --- TENGAH: Grafik / Tabel ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        analyticsCard = createAnalyticsCard();
        queueCard = createQueueManagementCard();
        cardPanel.add(analyticsCard, "ANALYTICS");
        cardPanel.add(queueCard, "QUEUE");

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(topContainer, BorderLayout.NORTH);
        centerContainer.add(cardPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(centerContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(ColorPalette.BACKGROUND);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Ambil data awal untuk statistik
        updateStatsValues();

        refreshScheduler = new QueueRefreshScheduler(() -> SwingUtilities.invokeLater(this::reloadDashboardData));
        refreshScheduler.start(5000, 5000);

        setVisible(true);
    }

    private JPanel createNavigationMenu() {
        JPanel navContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 236, 240)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        navContainer.setOpaque(false);
        navContainer.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        btnAnalytics = createPillButton("Analitik", true);
        btnQueue = createPillButton("Manajemen Antrian", false);

        btnAnalytics.addActionListener(e -> switchView("ANALYTICS", btnAnalytics, btnQueue));
        btnQueue.addActionListener(e -> switchView("QUEUE", btnQueue, btnAnalytics));

        navContainer.add(btnAnalytics);
        navContainer.add(btnQueue);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(navContainer);

        return wrapper;
    }

    private void switchView(String cardName, JButton activeBtn, JButton inactiveBtn) {
        cardLayout.show(cardPanel, cardName);
        currentCardName = cardName;

        activeBtn.setBackground(Color.WHITE);
        activeBtn.setForeground(Color.BLACK);
        activeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        inactiveBtn.setBackground(new Color(235, 236, 240));
        inactiveBtn.setForeground(Color.DARK_GRAY);
        inactiveBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JButton createPillButton(String text, boolean isActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getBackground() == Color.WHITE) { 
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                }
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width + 40, 40);
            }
        };

        btn.setFont(isActive ? new Font("Segoe UI", Font.BOLD, 14) : new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));

        if (isActive) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        } else {
            btn.setBackground(new Color(235, 236, 240));
            btn.setForeground(Color.DARK_GRAY);
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (btn.getBackground() != Color.WHITE) {
                    btn.setForeground(Color.BLACK);
                }
            }

            public void mouseExited(MouseEvent evt) {
                if (btn.getBackground() != Color.WHITE) {
                    btn.setForeground(Color.DARK_GRAY);
                }
            }
        });

        return btn;
    }

    class ModernPanel extends JPanel {
        private int radius = 20;

        public ModernPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 245)),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        backButton.setForeground(ColorPalette.TEXT_PRIMARY);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            // Back button behavior lama (fallback)
            SwingUtilities.getWindowAncestor(this).requestFocus();
        });
        // Tambah callback ke container luar (mis. DisplayBoardFrame) supaya bisa kembali ke menu utama
        if (onBack != null) {
            backButton.addActionListener(e -> onBack.run());
        }

        JLabel titleLabel = new JLabel("Ruang Sehat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.PRIMARY);

        JLabel subTitle = new JLabel("Dashboard Admin");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subTitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(subTitle);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(backButton);
        leftContainer.add(titleContainer);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        profilePanel.setOpaque(false);

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(130, 100, 255));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String initial = "A";
                g2.drawString(initial, (getWidth() - fm.stringWidth(initial)) / 2,
                        ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() - 2);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(35, 35));
        avatarPanel.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Admin");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        JLabel roleLabel = new JLabel("Super Admin");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        textPanel.add(nameLabel);
        textPanel.add(roleLabel);

        profilePanel.add(avatarPanel);
        profilePanel.add(textPanel);

        headerPanel.add(leftContainer, BorderLayout.WEST);
        headerPanel.add(profilePanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(ColorPalette.BACKGROUND);
        // Tinggikan area kartu supaya konten tidak menyentuh tepi bawah
        statsPanel.setPreferredSize(new Dimension(0, 140));

        statsPanel.add(createStatCard("Total Pasien", lblTotalPatients, "👥", new Color(130, 100, 255)));
        statsPanel.add(createStatCard("Rerata Tunggu", lblAvgWait, "🕒", new Color(80, 80, 100)));
        statsPanel.add(createStatCard("Loket Aktif", lblActiveCounters, "🖥", new Color(75, 0, 130)));
        statsPanel.add(createStatCard("Antrian Valid", lblValidQueues, "✅", new Color(0, 200, 83)));

        return statsPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color iconBgColor) {
        ModernPanel card = new ModernPanel(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        // Tambah sedikit padding vertikal agar kartu terasa lebih tinggi dan seimbang
        content.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        // Perbesar panel ikon dan buat benar‑benar 1:1 agar ikon berada di tengah kartu kecilnya
        Dimension iconSize = new Dimension(48, 48);
        iconPanel.setPreferredSize(iconSize);
        iconPanel.setMinimumSize(iconSize);
        iconPanel.setMaximumSize(iconSize);
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new GridBagLayout());
        iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setForeground(Color.WHITE);
        iconPanel.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label nilai diteruskan dari luar supaya bisa di‑update saat data berubah
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Gunakan glue di atas dan bawah supaya konten berada di tengah kartu
        content.add(Box.createVerticalGlue());
        content.add(iconPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(2));
        content.add(valueLabel);
        content.add(Box.createVerticalGlue());

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAnalyticsCard() {
        ModernPanel card = new ModernPanel(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel title = new JLabel("Statistik Departemen");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel subtitle = new JLabel("Distribusi pasien per poli (hari ini)");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel textContainer = new JPanel(new GridLayout(2, 1));
        textContainer.setOpaque(false);
        textContainer.add(title);
        textContainer.add(subtitle);
        header.add(textContainer, BorderLayout.CENTER);

        JPanel chartPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        chartPlaceholder.setOpaque(false);
        chartPlaceholder.setLayout(new GridLayout(0, 1, 10, 5));
        chartPlaceholder.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));

        QueueDao queueDao = new QueueDao();
        java.util.List<QueueDao.PoliCount> counts = queueDao.loadPoliCountsToday();

        if (counts.isEmpty()) {
            JLabel empty = new JLabel("Belum ada data antrian hari ini.");
            empty.setForeground(ColorPalette.TEXT_SECONDARY);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            chartPlaceholder.setLayout(new BorderLayout());
            chartPlaceholder.add(empty, BorderLayout.CENTER);
        } else {
            int max = 1;
            for (QueueDao.PoliCount pc : counts) {
                if (pc.total > max) {
                    max = pc.total;
                }
            }
            Color[] colors = {
                    new Color(130, 100, 255),
                    new Color(244, 67, 54),
                    new Color(76, 175, 80),
                    new Color(255, 193, 7),
                    new Color(0, 188, 212),
                    new Color(103, 58, 183)
            };
            int i = 0;
            for (QueueDao.PoliCount pc : counts) {
                Color c = colors[i % colors.length];
                chartPlaceholder.add(createBarRow(pc.poliName, pc.total, max, c));
                i++;
            }
        }

        card.add(header, BorderLayout.NORTH);
        card.add(chartPlaceholder, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBarRow(String label, int value, int max, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(value + " pasien");
        bar.setForeground(barColor);
        bar.setBackground(new Color(240, 240, 245));

        row.add(lbl, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);

        return row;
    }

    private JPanel createQueueManagementCard() {
        ModernPanel card = new ModernPanel(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel title = new JLabel("Aktivitas Terkini");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel subtitle = new JLabel("Pemantauan antrian real-time");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel textContainer = new JPanel(new GridLayout(2, 1));
        textContainer.setOpaque(false);
        textContainer.add(title);
        textContainer.add(subtitle);
        header.add(textContainer, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlPanel.setOpaque(false);

        JLabel loketLabel = new JLabel("Loket:");
        loketLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        LoketDao loketDao = new LoketDao();
        java.util.List<LoketDao.Loket> lokets = loketDao.findAllActive();
        loketCombo = new JComboBox<>();
        for (LoketDao.Loket l : lokets) {
            loketCombo.addItem(new LoketItem(l.getId(), l.getName()));
        }
        loketCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loketCombo.setPreferredSize(new Dimension(120, 32));
        loketCombo.setMaximumSize(new Dimension(150, 32));
        loketCombo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));

        JButton btnCallNext = new ModernButton("Panggil Berikutnya",
                ColorPalette.PRIMARY,
                ColorPalette.PRIMARY_DARK);
        JButton btnRecall = new ModernButton("Panggil Ulang",
                ColorPalette.PRIMARY,
                ColorPalette.PRIMARY_DARK);
        JButton btnFinish = new ModernButton("Selesai",
                ColorPalette.SUCCESS,
                ColorPalette.SUCCESS.darker());
        JButton btnCancel = new ModernButton("Batal",
                ColorPalette.DANGER,
                ColorPalette.DANGER.darker());

        btnCallNext.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRecall.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFinish.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnCallNext.addActionListener(e -> handleCallNext());
        btnRecall.addActionListener(e -> handleRecall());
        btnFinish.addActionListener(e -> handleFinish());
        btnCancel.addActionListener(e -> handleCancel());

        controlPanel.add(loketLabel);
        controlPanel.add(loketCombo);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(btnCallNext);
        controlPanel.add(btnRecall);
        controlPanel.add(btnFinish);
        controlPanel.add(btnCancel);

        String[] columnNames = { "No. Antrian", "Nama Pasien", "Poli", "Waktu Tunggu", "Status" };

        QueueDao queueDao = new QueueDao();
        java.util.List<QueueDao.QueueItem> items = queueDao.loadRecentActivities(50);
        Object[][] data = new Object[items.size()][5];
        int i = 0;
        for (QueueDao.QueueItem item : items) {
            data[i][0] = item.nomorAntrian;
            data[i][1] = item.patientName;
            data[i][2] = item.poliName;
            data[i][3] = formatWaitingTime(item);
            data[i][4] = mapStatusLabel(item.status);
            i++;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        this.queueTable = table;
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setSelectionForeground(ColorPalette.TEXT_PRIMARY);
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setForeground(ColorPalette.TEXT_SECONDARY);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)));

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = new JLabel((String) value);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setHorizontalAlignment(CENTER);
                label.setOpaque(true);

                String status = (String) value;
                if (status.equals("Dipanggil")) {
                    label.setBackground(new Color(130, 100, 255));
                    label.setForeground(Color.WHITE);
                } else if (status.equals("Selesai")) {
                    label.setBackground(new Color(0, 200, 83));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(new Color(230, 230, 250));
                    label.setForeground(new Color(50, 50, 150));
                }

                label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                panel.add(label);
                return panel;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tableScroll.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(controlPanel, BorderLayout.CENTER);
        card.add(tableScroll, BorderLayout.SOUTH);

        return card;
    }

    private String mapStatusLabel(String status) {
        if (status == null) return "";
        switch (status) {
            case "MENUNGGU":
                return "Menunggu";
            case "DIPANGGIL":
                return "Dipanggil";
            case "SELESAI":
                return "Selesai";
            case "BATAL":
                return "Batal";
            default:
                return status;
        }
    }

    private String formatWaitingTime(QueueDao.QueueItem item) {
        if (item.createdAt == null) {
            return "-";
        }
        long endMillis;
        if (item.finishedAt != null) {
            endMillis = item.finishedAt.getTime();
        } else if (item.calledAt != null) {
            endMillis = item.calledAt.getTime();
        } else {
            endMillis = System.currentTimeMillis();
        }
        long diffMinutes = Math.max(0, (endMillis - item.createdAt.getTime()) / (60 * 1000));
        return diffMinutes + " mnt";
    }

    private void updateStatsValues() {
        DashboardStatsDao dao = new DashboardStatsDao();
        DashboardStatsDao.Overview overview = dao.loadOverview();

        if (lblTotalPatients != null) {
            lblTotalPatients.setText(String.valueOf(overview.totalPatients));
        }
        if (lblAvgWait != null) {
            lblAvgWait.setText(overview.averageWaitMinutes + " mnt");
        }
        if (lblActiveCounters != null) {
            lblActiveCounters.setText(String.valueOf(overview.activeCounters));
        }
        if (lblValidQueues != null) {
            lblValidQueues.setText(String.valueOf(overview.validQueues));
        }
    }

    private void reloadDashboardData() {
        // Perbarui ringkasan statistik di kartu atas
        updateStatsValues();

        analyticsCard = createAnalyticsCard();
        queueCard = createQueueManagementCard();
        cardPanel.removeAll();
        cardPanel.add(analyticsCard, "ANALYTICS");
        cardPanel.add(queueCard, "QUEUE");
        cardPanel.revalidate();
        cardPanel.repaint();
        cardLayout.show(cardPanel, currentCardName);
    }

    private LoketItem getSelectedLoket() {
        Object selected = loketCombo != null ? loketCombo.getSelectedItem() : null;
        return (selected instanceof LoketItem) ? (LoketItem) selected : null;
    }

    private void handleCallNext() {
        LoketItem loket = getSelectedLoket();
        if (loket == null) {
            JOptionPane.showMessageDialog(this, "Pilih loket terlebih dahulu.", "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        QueueDao dao = new QueueDao();

        if (dao.hasActiveForLoket(loket.id)) {
            JOptionPane.showMessageDialog(this,
                    "Loket " + loket.name + " masih melayani pasien.\n" +
                            "Selesaikan terlebih dahulu atau pilih loket lain.",
                    "Loket Masih Terpakai",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        QueueDao.QueueItem item = dao.callNextForLoket(loket.id);
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada antrian MENUNGGU.", "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Memanggil nomor " + item.nomorAntrian + " (" + item.patientName + ") di " + loket.name,
                    "Panggil Berikutnya", JOptionPane.INFORMATION_MESSAGE);
        }
        reloadDashboardData();
    }

    private void handleFinish() {
        String nomor = getSelectedNomorAntrian();
        if (nomor == null) return;
        QueueDao dao = new QueueDao();
        dao.markFinishedByNomor(nomor);
        reloadDashboardData();
    }

    private void handleCancel() {
        String nomor = getSelectedNomorAntrian();
        if (nomor == null) return;
        QueueDao dao = new QueueDao();
        dao.markCancelledByNomor(nomor);
        reloadDashboardData();
    }

    private void handleRecall() {
        String nomor = getSelectedNomorAntrian();
        if (nomor == null) return;
        QueueDao dao = new QueueDao();
        dao.recallByNomor(nomor);
        reloadDashboardData();
    }

    private String getSelectedNomorAntrian() {
        if (queueTable == null || queueTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris antrian terlebih dahulu.", "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int row = queueTable.getSelectedRow();
        Object value = queueTable.getValueAt(row, 0);
        return value != null ? value.toString() : null;
    }

    private static class LoketItem {
        final int id;
        final String name;

        LoketItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
