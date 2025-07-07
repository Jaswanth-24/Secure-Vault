import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.JSONObject;

public class DashboardUI extends JFrame {

    private DefaultListModel<String> fileModel = new DefaultListModel<>();
    private Map<String, String> filePathMap = new HashMap<>();
    private Map<String, String> originalPathMap = new HashMap<>();
    private String username;
    private File pathStoreFile;

    public DashboardUI(String username) {
        this.username = username;
        pathStoreFile = new File("user_data", username + "_paths.json");
        loadOriginalPaths();

        setTitle("Secure Vault - Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        sidebar.setBackground(new Color(34, 40, 49));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        String[] navItems = {"Dashboard", "Encrypt File", "Settings", "Logout"};
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(160, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(57, 62, 70));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(btn);

            switch (item) {
                case "Dashboard" -> btn.addActionListener(e -> refreshFileList());

                case "Encrypt File" -> btn.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int option = fileChooser.showOpenDialog(this);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File originalFile = fileChooser.getSelectedFile();
                        File userDir = new File("encrypted", username);
                        userDir.mkdirs();
                        File copyFile = new File(userDir, originalFile.getName());
                        try {
                            Crypto.encryptInPlace(originalFile.getAbsolutePath()); // Encrypt original
                            Files.copy(originalFile.toPath(), copyFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING); // Store copy
                            originalPathMap.put(copyFile.getName(), originalFile.getAbsolutePath());
                            saveOriginalPaths();
                            DashboardUtil.logActivity(username + " encrypted: " + originalFile.getName());
                            JOptionPane.showMessageDialog(this, "✅ File Encrypted: " + originalFile.getName());
                            refreshFileList();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "❌ Encryption Failed: " + ex.getMessage());
                        }
                    }
                });

                case "Settings" -> btn.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this, "⚙️ Settings coming soon!");
                });

                case "Logout" -> btn.addActionListener(e -> {
                    dispose();
                    new LoginUI();
                });
            }
        }

        JLabel header = new JLabel("Hello, " + username);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBackground(Color.WHITE);
        filePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel fileLabel = new JLabel("🔐 Your Encrypted Files");
        fileLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JList<String> fileList = new JList<>(fileModel);
        fileList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(fileList);

        JButton decryptButton = new JButton("🔓 Decrypt Selected");
        decryptButton.setBackground(new Color(0, 102, 204));
        decryptButton.setForeground(Color.WHITE);
        decryptButton.setFocusPainted(false);
        decryptButton.setFont(new Font("Arial", Font.PLAIN, 14));
        decryptButton.addActionListener(e -> {
            String selected = fileList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a file to decrypt.");
                return;
            }
            try {
                String encryptedPath = filePathMap.get(selected);
                Crypto.decryptInPlace(encryptedPath);

                String originalPath = originalPathMap.get(selected);
                if (originalPath != null) {
                    Crypto.decryptInPlace(originalPath);
                }

                DashboardUtil.logActivity(username + " decrypted: " + selected);
                JOptionPane.showMessageDialog(this, "✅ Decrypted: " + selected);

                // Remove encrypted copy and update tracking
                new File(encryptedPath).delete();
                originalPathMap.remove(selected);
                saveOriginalPaths();

                refreshFileList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Decryption Failed: " + ex.getMessage());
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(decryptButton);

        filePanel.add(fileLabel, BorderLayout.NORTH);
        filePanel.add(scrollPane, BorderLayout.CENTER);
        filePanel.add(bottomPanel, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(filePanel, BorderLayout.CENTER);

        refreshFileList();
        setVisible(true);
    }

    private void refreshFileList() {
        fileModel.clear();
        filePathMap.clear();
        File userDir = new File("encrypted", username);
        File[] files = userDir.listFiles();
        if (files != null) {
            for (File f : files) {
                fileModel.addElement(f.getName());
                filePathMap.put(f.getName(), f.getAbsolutePath());
            }
        }
    }

    private void saveOriginalPaths() {
        try {
            JSONObject json = new JSONObject(originalPathMap);
            File userDataDir = new File("user_data");
            userDataDir.mkdirs();
            try (FileWriter writer = new FileWriter(pathStoreFile)) {
                writer.write(json.toString());
            }
        } catch (IOException e) {
            System.err.println("Failed to save original paths: " + e.getMessage());
        }
    }

    private void loadOriginalPaths() {
        if (!pathStoreFile.exists()) return;
        try {
            String content = Files.readString(pathStoreFile.toPath());
            JSONObject json = new JSONObject(content);
            for (String key : json.keySet()) {
                originalPathMap.put(key, json.getString(key));
            }
        } catch (IOException e) {
            System.err.println("Failed to load original paths: " + e.getMessage());
        }
    }
}