import javax.swing.*;
import java.awt.*;

public class RegisterUI extends JFrame {
    public RegisterUI() {
        setTitle("Secure Vault - Register");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField roleField = new JTextField();

        add(new JLabel("Username:"));
        add(usernameField);

        add(new JLabel("Password:"));
        add(passwordField);

        add(new JLabel("Role (admin/user):"));
        add(roleField);

        JButton registerButton = new JButton("Register");
        add(registerButton);

        registerButton.addActionListener(e -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            String role = roleField.getText().trim();

            if (user.isEmpty() || pass.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            User newUser = new User(user, pass, role);
            boolean success = UserDB.addUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Registered Successfully");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Username already exists!");
            }
        });

        setVisible(true);
    }
}
