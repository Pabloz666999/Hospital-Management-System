import components.ColorPalette;
import components.ModernButton;
import components.ModernTextField;
import components.RoundedBorder;
import db.PoliDao;
import db.QueueDao;
import model.Patient;
import model.Polyclinic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatientRegistration extends JPanel {

    private final PoliDao poliDao = new PoliDao();
    private PoliDao.Poli selectedPoli;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private ModernTextField nameField;
    private ModernTextField phoneField;
    private ModernTextField nikField;
    private JTextArea addressArea;
    private JSpinner ageSpinner;
    private JComboBox<String> genderCombo;
    private JSpinner dobSpinner;

    private String lastQueueNumber;
    private String lastPatientName;

    private String currentStep = "STEP1";

    private Runnable onBack;

    public PatientRegistration() {
        this(null);
    }

    public PatientRegistration(Runnable onBack) {
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(ColorPalette.BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(ColorPalette.BACKGROUND);

        cardPanel.add(createServiceSelectionPanel(), "STEP1");
        cardPanel.add(createPatientFormPanel(), "STEP2");
        cardPanel.add(createConfirmationPanel(), "STEP3");

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        showStep1();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 245)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        backButton.setForeground(ColorPalette.TEXT_PRIMARY);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            if ("STEP1".equals(currentStep)) {
                if (onBack != null) {
                    onBack.run();
                }
            } else {
                showStep1();
            }
        });

        JLabel titleLabel = new JLabel("Registrasi Pasien oleh Petugas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorPalette.PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        emptyPanel.setPreferredSize(backButton.getPreferredSize());

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(emptyPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createServiceSelectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ColorPalette.BACKGROUND);
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JLabel title = new JLabel("Pilih Poli Tujuan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ColorPalette.PRIMARY);

        JLabel subtitle = new JLabel("Pilih layanan yang akan dituju pasien");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(ColorPalette.BACKGROUND);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JPanel servicesPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        servicesPanel.setBackground(ColorPalette.BACKGROUND);

        List<PoliDao.Poli> allPoli = poliDao.findAll();
        container.add(titlePanel, BorderLayout.NORTH);

        if (allPoli.isEmpty()) {
            JLabel empty = new JLabel("Belum ada data poli di database.");
            empty.setForeground(Color.GRAY);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            container.add(empty, BorderLayout.CENTER);
        } else {
            for (PoliDao.Poli poli : allPoli) {
                servicesPanel.add(createPoliCard(poli));
            }
            JScrollPane scroll = new JScrollPane(servicesPanel);
            scroll.setBorder(null);
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getViewport().setBackground(ColorPalette.BACKGROUND);
            container.add(scroll, BorderLayout.CENTER);
        }

        return container;
    }

    private JPanel createPoliCard(PoliDao.Poli poli) {
        JPanel card = new JPanel() {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 18;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setStroke(new BasicStroke(hovered ? 2f : 1f));
                g2.setColor(hovered ? ColorPalette.PRIMARY : new Color(230, 230, 235));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(getIconForPoli(poli.getName()));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(poli.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(poli.getDescription() != null ? poli.getDescription() : "");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Centerkan konten secara vertikal di dalam card
        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedPoli = poli;
                showStep2();
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createPatientFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ColorPalette.BACKGROUND);
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(235, 235, 240), 1, 18),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        JLabel title = new JLabel("Data Pasien");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ColorPalette.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new ModernTextField("");
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        phoneField = new ModernTextField("");
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        nikField = new ModernTextField("");
        nikField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        addressScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        ageSpinner = new JSpinner(new SpinnerNumberModel(25, 0, 120, 1));
        ageSpinner.setPreferredSize(new Dimension(100, 32));
        ageSpinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        JComponent ageEditor = ageSpinner.getEditor();
        if (ageEditor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) ageEditor).getTextField();
            tf.setHorizontalAlignment(SwingConstants.LEFT);
            tf.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }

        genderCombo = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        genderCombo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        genderCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                return lbl;
            }
        });

        dobSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(dateEditor);
        dobSpinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        JComponent dobEditor = dobSpinner.getEditor();
        if (dobEditor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) dobEditor).getTextField();
            tf.setHorizontalAlignment(SwingConstants.LEFT);
            tf.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(createLabeledField("Nama Lengkap", nameField));
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabeledField("Nomor Telepon", phoneField));
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabeledField("Nomor Induk Kependudukan (NIK)", nikField));
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabeledField("Umur", ageSpinner));
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabeledField("Jenis Kelamin", genderCombo));
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabeledField("Tanggal Lahir", dobSpinner));
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(createLabeledField("Alamat Lengkap", addressScroll));
        formCard.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        ModernButton backButton = new ModernButton("Kembali",
                Color.WHITE,
                new Color(240, 240, 255));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setForeground(ColorPalette.PRIMARY);
        backButton.setPreferredSize(new Dimension(120, 36));
        backButton.addActionListener(e -> showStep1());

        ModernButton submitButton = new ModernButton("Daftarkan",
                ColorPalette.PRIMARY,
                ColorPalette.PRIMARY_DARK);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitButton.addActionListener(e -> handleSubmit());

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(formCard);
        content.add(Box.createVerticalStrut(15));
        content.add(buttonPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(ColorPalette.BACKGROUND);

        container.add(scroll, BorderLayout.CENTER);

        return container;
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        return panel;
    }

    private JPanel createConfirmationPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ColorPalette.BACKGROUND);
        container.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(235, 235, 240), 1, 18),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        JLabel title = new JLabel("Pendaftaran Berhasil");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ColorPalette.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel();
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setName("subtitleLabel");

        JLabel queueLabel = new JLabel();
        queueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        queueLabel.setForeground(ColorPalette.PRIMARY);
        queueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        queueLabel.setName("queueLabel");

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(queueLabel);
        card.add(Box.createVerticalStrut(10));

        JLabel info = new JLabel("Mohon duduk di ruang tunggu dan pantau layar antrian.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(ColorPalette.TEXT_SECONDARY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(info);
        card.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        ModernButton newRegBtn = new ModernButton("Pendaftaran Baru", ColorPalette.PRIMARY, ColorPalette.PRIMARY_DARK);
        newRegBtn.addActionListener(e -> showStep1());

        ModernButton closeBtn = new ModernButton("Tutup",
                Color.WHITE,
                new Color(240, 240, 255));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setForeground(ColorPalette.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(120, 36));
        closeBtn.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        buttonPanel.add(newRegBtn);
        buttonPanel.add(closeBtn);

        card.add(buttonPanel);

        container.add(card, BorderLayout.CENTER);
        return container;
    }

    private void handleSubmit() {
        if (selectedPoli == null) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih poli terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            showStep1();
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama pasien wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String phone = phoneField.getText().trim();
        String nik = nikField.getText().trim();
        String address = addressArea.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String gender = (String) genderCombo.getSelectedItem();
        Date dob = (Date) dobSpinner.getValue();

        String genderCode = null;
        if ("Laki-laki".equals(gender)) {
            genderCode = "L";
        } else if ("Perempuan".equals(gender)) {
            genderCode = "P";
        }

        // Bentuk objek domain Patient + Polyclinic (lapisan model)
        Polyclinic targetPoli = null;
        if (selectedPoli != null) {
            targetPoli = new Polyclinic(
                    selectedPoli.getName(),
                    selectedPoli.getDescription(),
                    selectedPoli.getPrefix()
            );
        }
        Patient patientModel = new Patient(
                0, // id akan diisi oleh database
                name,
                phone,
                nik,
                targetPoli
        );

        // Mapping ke DTO yang dipakai DAO untuk simpan ke database
        QueueDao.PatientData patientData = new QueueDao.PatientData(
                patientModel.getName(),
                patientModel.getPhoneNumber(),
                patientModel.getidentificationNumber(),
                age, genderCode, address, dob
        );

        QueueDao queueDao = new QueueDao();
        String nomor = queueDao.registerNewQueue(patientData, selectedPoli);
        if (nomor == null) {
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan saat menyimpan data ke database.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        lastQueueNumber = nomor;
        lastPatientName = name;
        showStep3();
    }

    private void showStep1() {
        currentStep = "STEP1";
        selectedPoli = null;
        nameField.setText("");
        phoneField.setText("");
        nikField.setText("");
        addressArea.setText("");
        ageSpinner.setValue(25);
        genderCombo.setSelectedIndex(0);
        dobSpinner.setValue(new Date());
        cardLayout.show(cardPanel, "STEP1");
    }

    private void showStep2() {
        currentStep = "STEP2";
        cardLayout.show(cardPanel, "STEP2");
    }

    private void showStep3() {
        for (Component comp : ((JPanel) ((JPanel) cardPanel.getComponent(2)).getComponent(0)).getComponents()) {
            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                if ("subtitleLabel".equals(lbl.getName())) {
                    String poliName = selectedPoli != null ? selectedPoli.getName() : "-";
                    lbl.setText("Pasien: " + lastPatientName + " | Poli: " + poliName);
                } else if ("queueLabel".equals(lbl.getName())) {
                    lbl.setText(lastQueueNumber);
                }
            }
        }
        currentStep = "STEP3";
        cardLayout.show(cardPanel, "STEP3");
    }

    private String getIconForPoli(String poliName) {
        if (poliName == null) {
            return "🏥";
        }
        String lower = poliName.toLowerCase();
        if (lower.contains("umum")) return "🏥";
        if (lower.contains("anak")) return "🧒";
        if (lower.contains("jantung")) return "❤️";
        if (lower.contains("igd")) return "🚑";
        if (lower.contains("lab")) return "🧪";
        if (lower.contains("radiologi")) return "🩻";
        if (lower.contains("farmasi")) return "💊";
        return "🏥";
    }
}
