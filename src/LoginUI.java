import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginUI() {
        setTitle("🔐 Secure Vault - Login");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Secure Vault", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setOpaque(true);
        header.setBackground(new Color(34, 40, 49));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 60));
        add(header, BorderLayout.NORTH);

        // Form panel setup
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        Dimension fieldSize = new Dimension(220, 30);

        usernameField.setPreferredSize(fieldSize);
        passwordField.setPreferredSize(fieldSize);

        // Username Label + Field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(usernameField, gbc);

        // Password Label + Field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton.setBackground(new Color(57, 62, 70));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 35));
        formPanel.add(loginButton, gbc);

        // Register Button
        gbc.gridy = 3;
        registerButton.setBackground(new Color(34, 40, 49));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.PLAIN, 13));
        registerButton.setPreferredSize(new Dimension(120, 30));
        formPanel.add(registerButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Action Listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> new RegisterUI());

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (UserDB.verifyCredentials(username, password)) {
            String role = UserDB.getRole(username);
            dispose();
            new DashboardUI(role); // pass key to Dashboard

        } else {
            JOptionPane.showMessageDialog(this, "❌ Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginUI();
    }
}
