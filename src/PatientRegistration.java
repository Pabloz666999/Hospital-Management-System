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

public class PatientRegistration extends JFrame {

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

    public PatientRegistration() {
        setTitle("Ruang Sehat - Registrasi Pasien");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(ColorPalette.BACKGROUND);

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

        add(mainPanel);

        showStep1();
        setVisible(true);
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
        backButton.addActionListener(e -> handleBackAction());

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
    
    private void handleBackAction() {
        if ("STEP1".equals(currentStep)) {
            dispose();
            new MainMenuFrame();
        } else {
            showStep1();
        }
    }

    // --- STEP 1: PILIH POLI ---
    private JPanel createServiceSelectionPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(ColorPalette.BACKGROUND);
        container.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel title = new JLabel("Pilih Poli Tujuan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ColorPalette.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Pilih layanan yang akan dituju pasien");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(ColorPalette.BACKGROUND);
        
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitle);
        titlePanel.add(Box.createVerticalStrut(30));

        JPanel servicesPanel = new JPanel(new GridLayout(0, 4, 25, 25));
        servicesPanel.setBackground(ColorPalette.BACKGROUND);

        List<PoliDao.Poli> allPoli = poliDao.findAll();
        container.add(titlePanel, BorderLayout.NORTH);

        if (allPoli.isEmpty()) {
            JLabel empty = new JLabel("Belum ada data poli di database.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(Color.GRAY);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            container.add(empty, BorderLayout.CENTER);
        } else {
            for (PoliDao.Poli poli : allPoli) {
                servicesPanel.add(createPoliCard(poli));
            }
            
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(ColorPalette.BACKGROUND);
            wrapper.add(servicesPanel, BorderLayout.NORTH);

            JScrollPane scroll = new JScrollPane(wrapper);
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
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 25;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setStroke(new BasicStroke(hovered ? 2f : 1f));
                g2.setColor(hovered ? ColorPalette.PRIMARY : new Color(230, 230, 235));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(200, 220));

        JLabel iconLabel = new JLabel(getIconForPoli(poli.getName()));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(poli.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + poli.getDescription() + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedPoli = poli;
                showStep2();
            }
        });

        // Panel pembungkus agar kartu memiliki margin di grid
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    // --- STEP 2: FORM DATA PASIEN (DIPERBAIKI) ---
    private JPanel createPatientFormPanel() {
        // Container Utama: Menggunakan GridBagLayout untuk memusatkan kartu di tengah layar
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(ColorPalette.BACKGROUND);

        // KARTU FORMULIR
        JPanel formCard = new JPanel(new GridBagLayout()); // Gunakan GridBagLayout di dalam kartu juga
        formCard.setBackground(Color.WHITE);
        
        // Ukuran Preferensi Kartu: Lebar 550px agar pas (tidak terlalu lebar/sempit)
        formCard.setPreferredSize(new Dimension(550, 720)); 
        
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(230, 230, 235), 1, 25),
                // Padding di dalam kartu diperbesar (Top, Left, Bottom, Right)
                BorderFactory.createEmptyBorder(30, 40, 40, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0); // Jarak antar elemen vertikal
        gbc.fill = GridBagConstraints.HORIZONTAL; // Komponen melebar penuhi kartu
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 1. JUDUL
        JLabel title = new JLabel("Data Pasien");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ColorPalette.PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER); // Rata tengah teks
        
        gbc.insets = new Insets(0, 0, 25, 0); // Jarak ekstra di bawah judul
        formCard.add(title, gbc);

        // 2. INPUT FIELDS
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 12, 0); // Reset jarak normal

        // Inisialisasi Komponen
        nameField = new ModernTextField("Masukkan nama lengkap");
        phoneField = new ModernTextField("Masukkan nomor telepon");
        nikField = new ModernTextField("Masukkan NIK");
        
        // Menambahkan Field (Label + Input)
        addFormRow(formCard, "Nama Lengkap", nameField, gbc);
        addFormRow(formCard, "Nomor Telepon", phoneField, gbc);
        addFormRow(formCard, "Nomor Induk Kependudukan (NIK)", nikField, gbc);

        // 3. BARIS GANDA (UMUR & GENDER)
        gbc.gridy++;
        JPanel rowDual = new JPanel(new GridLayout(1, 2, 20, 0)); // Grid 2 kolom, gap 20px
        rowDual.setOpaque(false);
        
        // Umur
        ageSpinner = new JSpinner(new SpinnerNumberModel(25, 0, 120, 1));
        styleSpinner(ageSpinner);
        rowDual.add(createCompactField("Umur", ageSpinner));
        
        // Gender
        genderCombo = new JComboBox<>(new String[]{"Laki-laki", "Perempuan"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genderCombo.setBackground(Color.WHITE);
        // Styling combobox agar mirip textfield
        genderCombo.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        rowDual.add(createCompactField("Jenis Kelamin", genderCombo));
        
        formCard.add(rowDual, gbc);

        // 4. TANGGAL LAHIR
        gbc.gridy++;
        dobSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(dateEditor);
        styleSpinner(dobSpinner);
        addFormRow(formCard, "Tanggal Lahir", dobSpinner, gbc);

        // 5. ALAMAT
        gbc.gridy++;
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        addFormRow(formCard, "Alamat Lengkap", addressScroll, gbc);

        // 6. TOMBOL AKSI
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0); // Jarak ekstra di atas tombol
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);

        ModernButton backButton = new ModernButton("Kembali", Color.WHITE, new Color(240, 240, 255));
        backButton.setForeground(ColorPalette.PRIMARY);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> showStep1());

        ModernButton submitButton = new ModernButton("Daftarkan", ColorPalette.PRIMARY, ColorPalette.PRIMARY_DARK);
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.addActionListener(e -> handleSubmit());

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        
        formCard.add(buttonPanel, gbc);

        // Tambahkan kartu ke ScrollPane utama (untuk layar kecil)
        // Gunakan GridBagLayout pada Container utama agar kartu berada di tengah
        container.add(formCard);
        
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(20);
        mainScroll.getViewport().setBackground(ColorPalette.BACKGROUND);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(mainScroll, BorderLayout.CENTER);
        
        return wrapper;
    }

    // Helper untuk menambahkan baris form (Label + Input) ke GridBagLayout
    private void addFormRow(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc) {
        gbc.gridy++;
        
        // Container baris (Label di atas, Input di bawah)
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ColorPalette.TEXT_PRIMARY);
        
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        
        panel.add(row, gbc);
    }

    // Helper untuk field kecil (Umur/Gender)
    private JPanel createCompactField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ColorPalette.TEXT_PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void styleSpinner(JSpinner spinner) {
        // Styling custom untuk JSpinner agar mirip ModernTextField
        spinner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ColorPalette.TEXT_SECONDARY, 1, 10),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setHorizontalAlignment(SwingConstants.LEFT);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tf.setBorder(null); // Hapus border bawaan textfield dalam spinner
            tf.setBackground(Color.WHITE);
        }
    }

    // --- STEP 3: KONFIRMASI ---
    // --- STEP 3: KONFIRMASI (UPDATED) ---
    private JPanel createConfirmationPanel() {
        JPanel container = new JPanel(new GridBagLayout()); // Center card
        container.setBackground(ColorPalette.BACKGROUND);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(500, 450)); // Fit size
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(230, 230, 235), 1, 25),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));

        JLabel title = new JLabel("Pendaftaran Berhasil");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ColorPalette.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel();
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ColorPalette.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setName("subtitleLabel");

        JLabel queueLabel = new JLabel();
        queueLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        queueLabel.setForeground(ColorPalette.PRIMARY);
        queueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        queueLabel.setName("queueLabel");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        ModernButton newRegBtn = new ModernButton("Pendaftaran Baru", ColorPalette.PRIMARY, ColorPalette.PRIMARY_DARK);
        // PERBAIKAN: Lebar diperbesar dari 160 menjadi 200 agar teks tidak terpotong (...)
        newRegBtn.setPreferredSize(new Dimension(200, 40)); 
        newRegBtn.addActionListener(e -> showStep1());

        ModernButton closeBtn = new ModernButton("Tutup", Color.WHITE, new Color(240, 240, 255));
        closeBtn.setForeground(ColorPalette.PRIMARY);
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.addActionListener(e -> {
            dispose();
            new MainMenuFrame();
        });

        buttonPanel.add(newRegBtn);
        buttonPanel.add(closeBtn);

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(queueLabel);
        card.add(Box.createVerticalStrut(40));
        card.add(buttonPanel);
        card.add(Box.createVerticalGlue());

        container.add(card);
        return container;
    }

    private void handleSubmit() {
        if (selectedPoli == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih poli terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE);
            showStep1();
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pasien wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String phone = phoneField.getText().trim();
        String nik = nikField.getText().trim();
        String address = addressArea.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String gender = (String) genderCombo.getSelectedItem();
        Date dob = (Date) dobSpinner.getValue();

        String genderCode = "Laki-laki".equals(gender) ? "L" : "P";

        Polyclinic targetPoli = new Polyclinic(selectedPoli.getName(), selectedPoli.getDescription(), selectedPoli.getPrefix());
        Patient patientModel = new Patient(0, name, phone, nik, targetPoli);

        QueueDao.PatientData patientData = new QueueDao.PatientData(
                patientModel.getName(), patientModel.getPhoneNumber(), patientModel.getidentificationNumber(),
                age, genderCode, address, dob
        );

        QueueDao queueDao = new QueueDao();
        String nomor = queueDao.registerNewQueue(patientData, selectedPoli);
        if (nomor == null) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
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
        if (poliName == null) return "🏥";
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