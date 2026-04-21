package ui;

import dao.ComplaintDAO;
import model.Complaint;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * ComplaintForm - Screen for filing a new complaint.
 * Accessible from the user Dashboard.
 */
public class ComplaintForm extends JFrame {

    private JTextField titleField;
    private JTextArea  descriptionArea;
    private JButton    submitButton;
    private JButton    cancelButton;

    private final User         currentUser;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    public ComplaintForm(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("File a New Complaint");
        setSize(480, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(231, 76, 60));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel titleLabel = new JLabel("File a New Complaint");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        // Title field
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        formPanel.add(makeLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleField.setPreferredSize(new Dimension(280, 30));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(2, 6, 2, 6)
        ));
        formPanel.add(titleField, gbc);

        // Description area
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(makeLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(7, 28);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(4, 6, 4, 6)
        ));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.setBackground(new Color(245, 247, 250));

        submitButton = makeButton("Submit Complaint", new Color(231, 76, 60));
        cancelButton = makeButton("Cancel", new Color(149, 165, 166));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Listeners
        submitButton.addActionListener(this::handleSubmit);
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleSubmit(ActionEvent e) {
        String title       = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Both title and description are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (title.length() > 100) {
            JOptionPane.showMessageDialog(this,
                "Title must not exceed 100 characters.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Complaint complaint = new Complaint(currentUser.getId(), title, description);
        boolean success = complaintDAO.fileComplaint(complaint);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Complaint submitted successfully!\nStatus: Pending",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to submit complaint. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helpers
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}