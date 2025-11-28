import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayBoardFrame extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;

    private static final String TICKET_CALLED = "A045";
    private static final String UNIT_DESTINATION = "Poli Umum";
    private static final String COUNTER_NUMBER = "3";

    public DisplayBoardFrame() {
        setTitle("Ruang Sehat - Layar Antrian");
        setSize(1000, 650);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
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

        startClockThread();

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftContainer.setOpaque(false);

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(e -> {
            dispose();
            new MainMenuFrame();
        });

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

        leftContainer.add(backButton);
        leftContainer.add(logoIcon);
        leftContainer.add(textPanel);

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

        header.add(leftContainer, BorderLayout.WEST);
        header.add(timePanel, BorderLayout.EAST);

        return header;
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

        JLabel numberLabel = new JLabel(TICKET_CALLED);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 90));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel unitLabel = new JLabel(UNIT_DESTINATION);
        unitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        unitLabel.setForeground(new Color(220, 220, 255));
        unitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        numberBox.add(numberLabel);
        numberBox.add(Box.createVerticalStrut(5));
        numberBox.add(unitLabel);

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
        counterBox.setPreferredSize(new Dimension(140, 50));
        counterBox.setMaximumSize(new Dimension(140, 50));
        counterBox.setLayout(new GridBagLayout());

        JLabel counterNum = new JLabel("LOKET " + COUNTER_NUMBER);
        counterNum.setFont(new Font("Segoe UI", Font.BOLD, 24));
        counterNum.setForeground(Color.WHITE);
        counterBox.add(counterNum);

        counterPanel.add(counterLabel);
        counterPanel.add(Box.createVerticalStrut(5));
        counterPanel.add(counterBox);
        counterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentWrapper.add(servingLabel);
        contentWrapper.add(Box.createVerticalStrut(10));
        contentWrapper.add(numberBox);
        contentWrapper.add(Box.createVerticalStrut(20));
        contentWrapper.add(counterPanel);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(contentWrapper);

        mainCard.add(centerWrapper, BorderLayout.CENTER);

        return mainCard;
    }

    private JPanel createRightSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel icon = new JLabel("🕒 ");
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel label = new JLabel("Daftar Antrian");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);

        titlePanel.add(icon);
        titlePanel.add(label);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        listContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        listContainer.add(Box.createVerticalStrut(5));

        listContainer.add(createQueueItem("1", "A046", "Poli Umum"));
        listContainer.add(Box.createVerticalStrut(10));

        listContainer.add(createQueueItem("2", "B023", "Kardiologi"));
        listContainer.add(Box.createVerticalStrut(10));

        listContainer.add(createQueueItem("3", "C012", "Laboratorium"));
        listContainer.add(Box.createVerticalStrut(10));

        listContainer.add(createQueueItem("4", "A047", "Poli Anak"));
        listContainer.add(Box.createVerticalStrut(10));

        listContainer.add(createQueueItem("5", "D008", "Radiologi"));

        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(listContainer);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Loket Sedang Melayani");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel counterListPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        counterListPanel.setOpaque(false);

        counterListPanel.add(createCounterBox("1", "A044"));
        counterListPanel.add(createCounterBox("2", "B022"));
        counterListPanel.add(createCounterBox("3", TICKET_CALLED));
        counterListPanel.add(createCounterBox("4", "A046"));
        counterListPanel.add(createCounterBox("5", "D007"));

        counterListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(titleLabel);
        bottomPanel.add(counterListPanel);
        bottomPanel.add(Box.createVerticalGlue());

        return bottomPanel;
    }

    private JPanel createCounterBox(String counterNum, String ticketNum) {
        JPanel counterBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 70, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        counterBox.setOpaque(false);
        counterBox.setPreferredSize(new Dimension(80, 100));
        counterBox.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

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

        counterBox.add(content);

        return counterBox;
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

    private void startClockThread() {
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DisplayBoardFrame());
    }
}