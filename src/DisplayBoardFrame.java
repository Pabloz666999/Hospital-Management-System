import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayBoardFrame extends JFrame {

    private JLabel timeLabel;
    private JLabel dateLabel;

    public DisplayBoardFrame() {
        setTitle("Ruang Sehat - Display Board");
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

        // 1. Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // 2. Content (Split Kiri & Kanan)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 30, 15);
        gbc.fill = GridBagConstraints.BOTH;

        // --- BAGIAN KIRI (NOW SERVING) ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        contentPanel.add(createLeftSection(), gbc);

        // --- BAGIAN KANAN (WAITING QUEUE) ---
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 15, 30, 30);
        contentPanel.add(createRightSection(), gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 3. Jalankan Thread untuk Jam
        startClockThread();

        add(mainPanel);
        setVisible(true);
    }

    // --- HEADER ---
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Panel Kiri: Back Button + Logo + Teks
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
        JPanel logoBg = new JPanel(new GridBagLayout());
        logoBg.setPreferredSize(new Dimension(50, 50));
        logoBg.setBackground(Color.WHITE);
        logoBg.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, width - 1, height - 1);
            }
        });
        logoBg.add(logoIcon);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("Ruang Sehat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Queue Status");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(200, 200, 255));

        textPanel.add(title);
        textPanel.add(subtitle);

        leftContainer.add(backButton);
        leftContainer.add(logoBg);
        leftContainer.add(textPanel);

        // Panel Kanan: Jam
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setOpaque(false);

        dateLabel = new JLabel("Loading date...");
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
        mainCard.setLayout(new GridBagLayout());
        mainCard.setOpaque(false);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);

        JLabel servingLabel = new JLabel("Now Serving");
        servingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
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
        numberBox.setPreferredSize(new Dimension(400, 200));
        numberBox.setMaximumSize(new Dimension(400, 200));
        numberBox.setLayout(new GridBagLayout());

        JLabel numberLabel = new JLabel("A045");
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 100));
        numberLabel.setForeground(Color.WHITE);
        numberBox.add(numberLabel);

        JLabel counterLabel = new JLabel("Counter");
        counterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
        JLabel counterNum = new JLabel("3");
        counterNum.setFont(new Font("Segoe UI", Font.BOLD, 36));
        counterNum.setForeground(Color.WHITE);
        counterBox.add(counterNum);

        contentWrapper.add(servingLabel);
        contentWrapper.add(Box.createVerticalStrut(15));
        contentWrapper.add(numberBox);
        contentWrapper.add(Box.createVerticalStrut(25));
        contentWrapper.add(counterLabel);
        contentWrapper.add(Box.createVerticalStrut(5));
        contentWrapper.add(counterBox);

        mainCard.add(contentWrapper);

        return mainCard;
    }

    private JPanel createRightSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel icon = new JLabel("🕒 ");
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel label = new JLabel("Waiting Queue");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        titlePanel.add(icon);
        titlePanel.add(label);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        listContainer.add(Box.createVerticalStrut(15));
        listContainer.add(createQueueItem("1", "A046", "General Medicine"));
        listContainer.add(Box.createVerticalStrut(10));
        listContainer.add(createQueueItem("2", "B023", "Cardiology"));
        listContainer.add(Box.createVerticalStrut(10));
        listContainer.add(createQueueItem("3", "C012", "Laboratory"));
        listContainer.add(Box.createVerticalStrut(10));
        listContainer.add(createQueueItem("4", "A047", "General Medicine"));
        listContainer.add(Box.createVerticalStrut(10));
        listContainer.add(createQueueItem("5", "D008", "Radiology"));

        panel.add(titlePanel);
        panel.add(listContainer);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createQueueItem(String index, String ticket, String dept) {
        JPanel item = new JPanel(new BorderLayout(15, 0)) {
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
        item.setPreferredSize(new Dimension(300, 70)); 
        item.setMaximumSize(new Dimension(1000, 70));
        item.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
        
        JPanel circleWrapper = new JPanel(new GridBagLayout()); 
        circleWrapper.setOpaque(false);
        circleWrapper.setPreferredSize(new Dimension(50, 70)); 
        
        JPanel circle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(150, 130, 255));
                g2.fillOval(0, 0, 40, 40); 
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(index);
                int stringAscent = fm.getAscent();
                
                g2.drawString(index, (40 - stringWidth) / 2, (40 + stringAscent) / 2 - 2);
            }
        };
        circle.setPreferredSize(new Dimension(41, 41)); 
        circle.setMinimumSize(new Dimension(41, 41));
        circle.setMaximumSize(new Dimension(41, 41));
        circle.setOpaque(false);
        
        circleWrapper.add(circle);
        
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.weightx = 1.0; 
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel ticketLbl = new JLabel(ticket);
        ticketLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ticketLbl.setForeground(Color.WHITE);
        
        JLabel deptLbl = new JLabel(dept);
        deptLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deptLbl.setForeground(new Color(220, 220, 255));
        
        gbc.gridy = 0; textPanel.add(ticketLbl, gbc);
        gbc.gridy = 1; textPanel.add(deptLbl, gbc);
        
        item.add(circleWrapper, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        
        return item;
    }

    private void startClockThread() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTime();
            }
        });
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
}