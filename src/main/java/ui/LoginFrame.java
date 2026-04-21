package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LoginFrame - The application entry screen.
 * Allows both admin and regular users to authenticate.
 */
public class LoginFrame extends JFrame {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private JButton        registerButton;
    private final UserDAO  userDAO = new UserDAO();

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Complaint Management System – Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main panel ──────────────────────────────────────────────────────
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header banner
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel titleLabel = new JLabel("Complaint Management System");
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

        // Email row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(makeLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        emailField = makeTextField();
        formPanel.add(emailField, gbc);

        // Password row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(makeLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        passwordField = new JPasswordField();
        styleField(passwordField);
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.setBackground(new Color(245, 247, 250));

        loginButton = makePrimaryButton("Login");
        registerButton = makeSecondaryButton("Register");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // ── Action listeners ────────────────────────────────────────────────
        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    /**
     * Validates credentials and routes the user to the appropriate dashboard.
     */
    private void handleLogin(ActionEvent e) {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic input validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        User user = userDAO.loginUser(email, password);

        if (user == null) {
            showError("Invalid email or password. Please try again.");
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Welcome, " + user.getName() + "!",
            "Login Successful", JOptionPane.INFORMATION_MESSAGE);

        // Route based on role
        if ("admin".equalsIgnoreCase(user.getRole())) {
            new AdminPanel(user).setVisible(true);
        } else {
            new Dashboard(user).setVisible(true);
        }
        dispose();
    }

    // ── UI helper methods ────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
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

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(149, 165, 166));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}