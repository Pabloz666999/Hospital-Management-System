import components.ColorPalette;
import components.ModernButton;
import components.ModernTextField;
import components.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class PatientRegistration extends JFrame {
    private int currentStep = 1;
    private String selectedService = "";
    private JPanel mainPanel;
    private JPanel contentPanel;

    public PatientRegistration() {
        setTitle("Ruang Sehat - Registrasi Pasien");
        setSize(1000, 650);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);

        createHeader();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorPalette.BACKGROUND);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        showServiceSelection();

        add(mainPanel);
        setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 245)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        backButton.setForeground(ColorPalette.TEXT_PRIMARY);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);

        backButton.addActionListener(e -> {
            if (currentStep == 2) {
                currentStep--;
                showServiceSelection();
            } else {
                dispose();
                new MainMenuFrame();
            }
        });

        JLabel titleLabel = new JLabel("Registrasi Pasien");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        emptyPanel.setPreferredSize(backButton.getPreferredSize());

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(emptyPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createStepIndicator(int currentStep) {
        JPanel stepPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        stepPanel.setBackground(ColorPalette.BACKGROUND);

        Color inactiveColor = new Color(240, 240, 245);
        Color inactiveTextColor = new Color(180, 180, 190);

        for (int i = 1; i <= 3; i++) {
            final int step = i;
            JPanel circle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (step <= currentStep) {
                        g2.setColor(ColorPalette.PRIMARY);
                        g2.fillOval(0, 0, 40, 40);
                        g2.setColor(Color.WHITE);
                    } else {
                        g2.setColor(inactiveColor);
                        g2.fillOval(0, 0, 40, 40);
                        g2.setColor(inactiveTextColor);
                    }

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    String text = (step < currentStep) ? "✓" : String.valueOf(step);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(text, (40 - fm.stringWidth(text)) / 2,
                            ((40 - fm.getHeight()) / 2) + fm.getAscent() - 2);
                }
            };
            circle.setPreferredSize(new Dimension(40, 40));
            circle.setOpaque(false);
            stepPanel.add(circle);

            if (i < 3) {
                final int lineStep = i;
                JPanel line = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (lineStep < currentStep)
                            g2.setColor(ColorPalette.PRIMARY);
                        else
                            g2.setColor(inactiveColor);
                        g2.setStroke(new BasicStroke(3));
                        g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
                    }
                };
                line.setPreferredSize(new Dimension(50, 40));
                line.setOpaque(false);
                stepPanel.add(line);
            }
        }
        return stepPanel;
    }

    private JPanel createServiceCard(String icon, String name) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

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

                int width = getWidth() - 1;
                int height = getHeight() - 1;
                int arc = 20;

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                if (isHovered) {
                    for (int i = 0; i < 4; i++) {
                        g2.setColor(new Color(ColorPalette.PRIMARY.getRed(), ColorPalette.PRIMARY.getGreen(),
                                ColorPalette.PRIMARY.getBlue(), 40 - (i * 10)));
                        g2.setStroke(new BasicStroke(1f + i));
                        g2.drawRoundRect(-i, -i, width + (i * 2), height + (i * 2), arc + i, arc + i);
                    }
                    g2.setColor(ColorPalette.PRIMARY);
                    g2.setStroke(new BasicStroke(1.5f));
                } else {
                    g2.setColor(new Color(235, 235, 240));
                    g2.setStroke(new BasicStroke(1f));
                }
                g2.drawRoundRect(0, 0, width, height, arc, arc);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(80, 80, 90));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(iconLabel, BorderLayout.CENTER);
        card.add(nameLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedService = name;
                currentStep = 2;
                showPatientInformation();
            }

            public void mouseEntered(MouseEvent e) {
                nameLabel.setForeground(ColorPalette.PRIMARY);
            }

            public void mouseExited(MouseEvent e) {
                nameLabel.setForeground(new Color(80, 80, 90));
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private void showServiceSelection() {
        contentPanel.removeAll();
        currentStep = 1;

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ColorPalette.BACKGROUND);

        container.add(createStepIndicator(1), BorderLayout.NORTH);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(ColorPalette.BACKGROUND);
        JLabel titleLabel = new JLabel("Pilih Poli");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));

        JPanel servicesPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        servicesPanel.setBackground(ColorPalette.BACKGROUND);
        servicesPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        String[][] services = {
                { "🏥", "Poli Umum" },
                { "🚑", "IGD (Instalasi Gawat Darurat)" },
                { "🫀", "Poli Jantung" },
                { "👶", "Poli Anak" },
                { "🦴", "Poli Ortopedi" },
                { "🔬", "Laboratorium" },
                { "📷", "Radiologi" },
                { "💊", "Farmasi" }
        };

        for (String[] service : services)
            servicesPanel.add(createServiceCard(service[0], service[1]));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ColorPalette.BACKGROUND);
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(servicesPanel, BorderLayout.CENTER);

        container.add(centerPanel, BorderLayout.CENTER);

        contentPanel.add(container);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPatientInformation() {
        contentPanel.removeAll();

        JPanel scrollContent = new JPanel(new GridBagLayout());
        scrollContent.setBackground(ColorPalette.BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);

        scrollContent.add(createStepIndicator(2), gbc);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(235, 235, 240), 1, 20),
                BorderFactory.createEmptyBorder(30, 40, 40, 40)));

        JLabel titleLabel = new JLabel("Data Pasien");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel serviceLabel = new JLabel("Poli: " + selectedService);
        serviceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        serviceLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        serviceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(titleLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(serviceLabel);
        formCard.add(Box.createVerticalStrut(25));

        JLabel nameLabel = new JLabel("Nama Lengkap");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernTextField nameField = new ModernTextField("");
        nameField.setMaximumSize(new Dimension(700, 40));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(nameLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(nameField);
        formCard.add(Box.createVerticalStrut(15));

        JPanel bioRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bioRow.setMaximumSize(new Dimension(700, 65));
        bioRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bioRow.setOpaque(false);

        JPanel dobPanel = new JPanel();
        dobPanel.setLayout(new BoxLayout(dobPanel, BoxLayout.Y_AXIS));
        dobPanel.setOpaque(false);
        JLabel dobLabel = new JLabel("Tanggal Lahir");
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dobLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        SpinnerDateModel dateModel = new SpinnerDateModel(new java.util.Date(), null, null,
                java.util.Calendar.DAY_OF_MONTH);
        JSpinner dobSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(dateEditor);

        dobSpinner.setPreferredSize(new Dimension(300, 40));
        dobSpinner.setMaximumSize(new Dimension(350, 40));
        dobSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        dobSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dobSpinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)));
        JComponent editorComp = dobSpinner.getEditor();
        if (editorComp instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editorComp).getTextField();
            tf.setHorizontalAlignment(JTextField.LEFT);
            tf.setBackground(Color.WHITE);
        }
        dobPanel.add(dobLabel);
        dobPanel.add(Box.createVerticalStrut(5));
        dobPanel.add(dobSpinner);

        JPanel genderPanel = new JPanel();
        genderPanel.setLayout(new BoxLayout(genderPanel, BoxLayout.Y_AXIS));
        genderPanel.setOpaque(false);
        JLabel genderLabel = new JLabel("Jenis Kelamin");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        genderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] genders = { "Laki-laki", "Perempuan" };
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderCombo.setBackground(Color.WHITE);
        genderCombo.setPreferredSize(new Dimension(300, 40));
        genderCombo.setMaximumSize(new Dimension(350, 40));
        genderCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        genderCombo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)));
        genderPanel.add(genderLabel);
        genderPanel.add(Box.createVerticalStrut(5));
        genderPanel.add(genderCombo);

        bioRow.add(dobPanel);
        bioRow.add(genderPanel);
        formCard.add(bioRow);
        formCard.add(Box.createVerticalStrut(15));

        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        rowPanel.setMaximumSize(new Dimension(700, 65));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setOpaque(false);

        JPanel phonePanel = new JPanel();
        phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.Y_AXIS));
        phonePanel.setOpaque(false);
        JLabel phoneLabel = new JLabel("Nomor Telepon");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ModernTextField phoneField = new ModernTextField("");
        phoneField.setPreferredSize(new Dimension(300, 40));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        phonePanel.add(phoneLabel);
        phonePanel.add(Box.createVerticalStrut(5));
        phonePanel.add(phoneField);

        JPanel agePanel = new JPanel();
        agePanel.setLayout(new BoxLayout(agePanel, BoxLayout.Y_AXIS));
        agePanel.setOpaque(false);
        JLabel ageLabel = new JLabel("Umur");
        ageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        SpinnerNumberModel ageModel = new SpinnerNumberModel(25, 0, 120, 1);
        JSpinner ageSpinner = new JSpinner(ageModel);
        ageSpinner.setPreferredSize(new Dimension(300, 40));
        ageSpinner.setMaximumSize(new Dimension(350, 40));
        ageSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        ageSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ageSpinner.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(10, 15, 10, 5)));
        JComponent editor = ageSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.LEFT);
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(Color.WHITE);
        }
        agePanel.add(ageLabel);
        agePanel.add(Box.createVerticalStrut(5));
        agePanel.add(ageSpinner);

        rowPanel.add(phonePanel);
        rowPanel.add(agePanel);
        formCard.add(rowPanel);
        formCard.add(Box.createVerticalStrut(15));

        JLabel nikLabel = new JLabel("Nomor Induk Kependudukan (NIK)");
        nikLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nikLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ModernTextField nikField = new ModernTextField("");
        nikField.setMaximumSize(new Dimension(700, 40));
        nikField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(nikLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(nikField);
        formCard.add(Box.createVerticalStrut(15));

        JLabel addressLabel = new JLabel("Alamat Lengkap");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setMaximumSize(new Dimension(700, 80));
        addressScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressScroll.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        formCard.add(addressLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(addressScroll);
        formCard.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setMaximumSize(new Dimension(700, 45));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setOpaque(false);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(ColorPalette.TEXT_PRIMARY);
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(new RoundedBorder(new Color(220, 220, 220), 2, 10));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            currentStep = 1;
            showServiceSelection();
        });

        ModernButton submitButton = new ModernButton("Submit", ColorPalette.SECONDARY, ColorPalette.PRIMARY);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Silakan masukkan nama pasien", "Validasi Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentStep = 3;
            showConfirmation(nameField.getText());
        });

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        formCard.add(buttonPanel);

        gbc.gridy = 1;
        scrollContent.add(formCard, gbc);

        JScrollPane mainScrollPane = new JScrollPane(scrollContent);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        contentPanel.add(mainScrollPane, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showConfirmation(String patientName) {
        contentPanel.removeAll();
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ColorPalette.BACKGROUND);
        container.add(createStepIndicator(3), BorderLayout.NORTH);

        JPanel scrollContent = new JPanel(new GridBagLayout());
        scrollContent.setBackground(ColorPalette.BACKGROUND);

        JPanel confirmCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        confirmCard.setOpaque(false);
        confirmCard.setLayout(new BorderLayout());
        confirmCard.setBackground(Color.WHITE);
        confirmCard.setPreferredSize(new Dimension(500, 420));
        confirmCard.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ColorPalette.SECONDARY, getWidth(), 0, ColorPalette.PRIMARY);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.fillRect(0, getHeight() - 10, getWidth(), 10);
            }
        };
        headerPanel.setPreferredSize(new Dimension(500, 55));
        headerPanel.setLayout(new GridBagLayout());
        headerPanel.setOpaque(false);

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setOpaque(false);

        JLabel confirmLabel = new JLabel("Konfirmasi");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerContent.add(confirmLabel);
        headerPanel.add(headerContent);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 25, 30));

        JLabel queueLabel = new JLabel("Nomor Antrian Anda");
        queueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        queueLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        queueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int queueNumber = (int) (Math.random() * 1000) + 1000;
        JLabel numberLabel = new JLabel(String.valueOf(queueNumber));
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        numberLabel.setForeground(ColorPalette.PRIMARY);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(queueLabel);
        bodyPanel.add(Box.createVerticalStrut(2));
        bodyPanel.add(numberLabel);
        bodyPanel.add(Box.createVerticalStrut(8));

        JPanel qrContainer = new JPanel(new GridBagLayout());
        qrContainer.setBackground(Color.WHITE);
        qrContainer.setBorder(new RoundedBorder(new Color(235, 235, 240), 1, 20));
        Dimension qrContainerSize = new Dimension(160, 160);
        qrContainer.setPreferredSize(qrContainerSize);
        qrContainer.setMinimumSize(qrContainerSize);
        qrContainer.setMaximumSize(qrContainerSize);
        qrContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel qrIconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);

                int qrWidth = 140;
                int startX = (getWidth() - qrWidth) / 2;
                int startY = (getHeight() - qrWidth) / 2;

                int blockSize = 10;
                int gridSize = qrWidth / blockSize;

                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        if (Math.random() > 0.5)
                            g2.fillRect(startX + i * blockSize, startY + j * blockSize, blockSize, blockSize);
                    }
                }

                g2.fillRect(startX, startY, 40, 40);
                g2.clearRect(startX + 10, startY + 10, 20, 20);
                g2.fillRect(startX + 15, startY + 15, 10, 10);
                g2.fillRect(startX + qrWidth - 40, startY, 40, 40);
                g2.clearRect(startX + qrWidth - 30, startY + 10, 20, 20);
                g2.fillRect(startX + qrWidth - 25, startY + 15, 10, 10);
                g2.fillRect(startX, startY + qrWidth - 40, 40, 40);
                g2.clearRect(startX + 10, startY + qrWidth - 30, 20, 20);
                g2.fillRect(startX + 15, startY + qrWidth - 25, 10, 10);
            }
        };
        Dimension qrIconSize = new Dimension(150, 150);
        qrIconPanel.setPreferredSize(qrIconSize);
        qrIconPanel.setMinimumSize(qrIconSize);
        qrIconPanel.setMaximumSize(qrIconSize);
        qrIconPanel.setBackground(Color.WHITE);
        qrContainer.add(qrIconPanel);

        JLabel scanLabel = new JLabel("Scan untuk informasi lebih lanjut");
        scanLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scanLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        scanLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(qrContainer);
        bodyPanel.add(Box.createVerticalStrut(5));
        bodyPanel.add(scanLabel);
        bodyPanel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setMaximumSize(new Dimension(450, 40));
        buttonPanel.setPreferredSize(new Dimension(450, 40));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setOpaque(false);

        ModernButton newRegButton = new ModernButton("Pendaftaran Baru", ColorPalette.PRIMARY,
                ColorPalette.PRIMARY_DARK);
        newRegButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newRegButton.addActionListener(e -> {
            currentStep = 1;
            showServiceSelection();
        });

        JButton printButton = new JButton("Cetak Tiket");
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printButton.setForeground(ColorPalette.PRIMARY);
        printButton.setBackground(Color.WHITE);
        printButton.setBorder(new RoundedBorder(ColorPalette.PRIMARY, 1, 10));
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.setFocusPainted(false);
        printButton.setContentAreaFilled(false);
        printButton.setOpaque(true);
        printButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Mencetak tiket antrian...", "Cetak",
                JOptionPane.INFORMATION_MESSAGE));

        buttonPanel.add(newRegButton);
        buttonPanel.add(printButton);
        bodyPanel.add(buttonPanel);

        confirmCard.add(headerPanel, BorderLayout.NORTH);
        confirmCard.add(bodyPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        scrollContent.add(confirmCard, gbc);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(container);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}