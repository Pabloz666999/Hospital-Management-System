import components.ColorPalette;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboardFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton btnAnalytics, btnQueue;

    public AdminDashboardFrame() {
        setTitle("Ruang Sehat - Dashboard Admin");
        setSize(1000, 650);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        contentPanel.add(topContainer, BorderLayout.NORTH);

        // --- TENGAH: Grafik / Tabel ---
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        cardPanel.add(createAnalyticsCard(), "ANALYTICS");
        cardPanel.add(createQueueManagementCard(), "QUEUE");

        contentPanel.add(cardPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
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
            dispose();
            new MainMenuFrame();
        });

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
        statsPanel.setPreferredSize(new Dimension(0, 100));

        statsPanel.add(createStatCard("Total Pasien", "143", "👥", new Color(130, 100, 255)));
        statsPanel.add(createStatCard("Rerata Tunggu", "12 mnt", "🕒", new Color(80, 80, 100)));
        statsPanel.add(createStatCard("Loket Aktif", "8", "🖥", new Color(75, 0, 130)));
        statsPanel.add(createStatCard("Antrian Valid", "135", "✅", new Color(0, 200, 83)));

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, String icon, Color iconBgColor) {
        ModernPanel card = new ModernPanel(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 15));

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
        iconPanel.setPreferredSize(new Dimension(38, 38));
        iconPanel.setMaximumSize(new Dimension(38, 38));
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new GridBagLayout());
        iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);
        iconPanel.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(iconPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(2));
        content.add(valueLabel);

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
        JLabel subtitle = new JLabel("Distribusi pasien per poli");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel textContainer = new JPanel(new GridLayout(2, 1));
        textContainer.setOpaque(false);
        textContainer.add(title);
        textContainer.add(subtitle);
        header.add(textContainer, BorderLayout.CENTER);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(45, "Pasien", "Umum");
        dataset.addValue(25, "Pasien", "IGD");
        dataset.addValue(18, "Pasien", "Jantung");
        dataset.addValue(32, "Pasien", "Anak");
        dataset.addValue(28, "Pasien", "Ortopedi");
        dataset.addValue(15, "Pasien", "Lab");
        dataset.addValue(10, "Pasien", "Radiologi");
        dataset.addValue(12, "Pasien", "Farmasi");

        JFreeChart barChart = ChartFactory.createBarChart(
                "", "", "", dataset,
                PlotOrientation.VERTICAL, false, true, false);

        barChart.setBackgroundPaint(Color.WHITE);
        barChart.setBorderVisible(false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(240, 240, 245));
        plot.setDomainGridlinesVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(130, 100, 255));
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.10);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        card.add(header, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);

        return card;
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

        String[] columnNames = { "No. Antrian", "Nama Pasien", "Poli", "Waktu Tunggu", "Status" };
        Object[][] data = {
                { "A045", "Budi Santoso", "Poli Umum", "15 mnt", "Dipanggil" },
                { "B023", "Siti Aminah", "Poli Jantung", "8 mnt", "Menunggu" },
                { "C012", "Rudi Hartono", "Laboratorium", "22 mnt", "Menunggu" },
                { "A047", "Dewi Lestari", "Poli Umum", "12 mnt", "Menunggu" },
                { "D008", "Andi Wijaya", "Radiologi", "5 mnt", "Menunggu" },
                { "E015", "Nina Karlina", "Poli Anak", "18 mnt", "Selesai" },
                { "F003", "Joko Susilo", "Farmasi", "3 mnt", "Menunggu" },
                { "G002", "Sari Indah", "IGD", "0 mnt", "Dipanggil" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setSelectionForeground(ColorPalette.TEXT_PRIMARY);
        table.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setBorder(BorderFactory.createEmptyBorder());

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
        card.add(tableScroll, BorderLayout.CENTER);

        return card;
    }
}