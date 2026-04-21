package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * RegisterFrame - New user registration screen.
 * Validates inputs and creates a new account in the database.
 */
public class RegisterFrame extends JFrame {

    private JTextField     nameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleCombo;
    private JButton        registerButton;
    private JButton        backButton;
    private final UserDAO  userDAO = new UserDAO();

    public RegisterFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Register – Complaint Management System");
        setSize(440, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel ──────────────────────────────────────────────────────
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(39, 174, 96));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(new EmptyBorder(20, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Name
        addFormRow(formPanel, gbc, 0, "Full Name:", nameField = makeTextField());

        // Email
        addFormRow(formPanel, gbc, 1, "Email:", emailField = makeTextField());

        // Password
        passwordField = new JPasswordField();
        styleField(passwordField);
        addFormRow(formPanel, gbc, 2, "Password:", passwordField);

        // Confirm Password
        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField);
        addFormRow(formPanel, gbc, 3, "Confirm Password:", confirmPasswordField);

        // Role selector
        roleCombo = new JComboBox<>(new String[]{"user", "admin"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.setPreferredSize(new Dimension(180, 30));
        addFormRow(formPanel, gbc, 4, "Role:", roleCombo);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.setBackground(new Color(245, 247, 250));

        registerButton = makePrimaryButton("Register", new Color(39, 174, 96));
        backButton     = makePrimaryButton("Back to Login", new Color(149, 165, 166));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // ── Listeners ───────────────────────────────────────────────────────
        registerButton.addActionListener(this::handleRegister);
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void handleRegister(ActionEvent e) {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String role    = (String) roleCombo.getSelectedItem();

        // ── Validation ───────────────────────────────────────────────────────
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (pass.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!pass.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        if (userDAO.emailExists(email)) {
            showError("This email is already registered.");
            return;
        }

        // ── Persist ──────────────────────────────────────────────────────────
        User newUser = new User(name, email, pass, role);
        boolean success = userDAO.registerUser(newUser);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Registration successful! Please login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    // ── UI helpers ───────────────────────────────────────────────────────────

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                             int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(makeLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField makeTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(180, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(2, 6, 2, 6)
        ));
    }

    private JButton makePrimaryButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}