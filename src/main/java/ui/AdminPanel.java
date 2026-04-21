package ui;

import dao.ComplaintDAO;
import model.Complaint;
import model.User;
import ui.ViewComplaints.StatusCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * AdminPanel - Full-featured admin screen.
 * Admins can view all complaints, update statuses, delete complaints, and logout.
 */
public class AdminPanel extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusCombo;
    private JButton           updateButton;
    private JButton           deleteButton;
    private JButton           refreshButton;
    private JTextField        searchField;
    private JButton           searchButton;

    private final User         currentUser;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    private static final String[] COLUMNS  = {"ID", "User", "Title", "Description", "Status", "Date"};
    private static final String[] STATUSES = {"Pending", "In Progress", "Resolved"};

    public AdminPanel(User user) {
        this.currentUser = user;
        initComponents();
        loadAllComplaints();
    }

    private void initComponents() {
        setTitle("Admin Panel – " + currentUser.getName());
        setSize(920, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 247, 250));

        // ══════════════════════════════════════════════════════════════════════
        //  HEADER — title left | user info + LOGOUT button right
        // ══════════════════════════════════════════════════════════════════════
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("Admin Control Panel – All Complaints");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        // Right side: user name + Logout button
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        JLabel userLabel = new JLabel("Admin: " + currentUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(189, 195, 199));
        headerRight.add(userLabel);

        // ── Logout button embedded in header ──────────────────────────────────
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(90, 28));
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                logoutBtn.setBackground(new Color(192, 57, 43));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                logoutBtn.setBackground(new Color(231, 76, 60));
            }
        });
        logoutBtn.addActionListener(e -> handleLogout());
        headerRight.add(logoutBtn);
        header.add(headerRight, BorderLayout.EAST);

        // ══════════════════════════════════════════════════════════════════════
        //  TOOLBAR — search | status update | delete | refresh
        // ══════════════════════════════════════════════════════════════════════
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(new Color(236, 240, 241));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 215, 220)));

        // Search
        toolbar.add(makeSmallLabel("Search ID:"));
        searchField = new JTextField(8);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toolbar.add(searchField);
        searchButton = makeToolbarButton("Search", new Color(52, 152, 219));
        toolbar.add(searchButton);
        JButton resetBtn = makeToolbarButton("Show All", new Color(149, 165, 166));
        toolbar.add(resetBtn);

        toolbar.add(makeSep());

        // Status update
        toolbar.add(makeSmallLabel("Set Status:"));
        statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(120, 28));
        toolbar.add(statusCombo);
        updateButton = makeToolbarButton("Update", new Color(230, 126, 34));
        toolbar.add(updateButton);

        toolbar.add(makeSep());

        // Delete
        deleteButton = makeToolbarButton("Delete", new Color(192, 57, 43));
        toolbar.add(deleteButton);

        toolbar.add(makeSep());

        // Refresh
        refreshButton = makeToolbarButton("Refresh", new Color(39, 174, 96));
        toolbar.add(refreshButton);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.add(header,  BorderLayout.NORTH);
        topArea.add(toolbar, BorderLayout.SOUTH);
        mainPanel.add(topArea, BorderLayout.NORTH);

        // ══════════════════════════════════════════════════════════════════════
        //  TABLE
        // ══════════════════════════════════════════════════════════════════════
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(8, 12, 0, 12));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ══════════════════════════════════════════════════════════════════════
        //  STATUS BAR
        // ══════════════════════════════════════════════════════════════════════
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(236, 240, 241));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 215, 220)));
        JLabel hint = new JLabel(
            "  Select a row → Update Status or Delete.  Press [Delete] key as shortcut.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setBorder(new EmptyBorder(5, 6, 5, 6));
        statusBar.add(hint, BorderLayout.WEST);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // ══════════════════════════════════════════════════════════════════════
        //  LISTENERS
        // ══════════════════════════════════════════════════════════════════════
        updateButton.addActionListener(e  -> handleUpdateStatus());
        deleteButton.addActionListener(e  -> handleDelete());
        refreshButton.addActionListener(e -> loadAllComplaints());
        searchButton.addActionListener(e  -> handleSearch());
        resetBtn.addActionListener(e      -> { searchField.setText(""); loadAllComplaints(); });

        // Keyboard Delete shortcut on table
        table.getInputMap(JComponent.WHEN_FOCUSED)
             .put(KeyStroke.getKeyStroke("DELETE"), "deleteRow");
        table.getActionMap().put("deleteRow",
            new javax.swing.AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleDelete();
                }
            });
    }

    // ── Load all complaints ────────────────────────────────────────────────────

    private void loadAllComplaints() {
        tableModel.setRowCount(0);
        List<Complaint> list = complaintDAO.getAllComplaints();
        if (list.isEmpty()) {
            tableModel.addRow(new Object[]{"–", "–", "No complaints in the system.", "", "", ""});
            return;
        }
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getUserName() != null ? c.getUserName() : "N/A",
                c.getTitle(),
                truncate(c.getDescription(), 45),
                c.getStatus(),
                c.getDate()
            });
        }
    }

    // ── Update status ──────────────────────────────────────────────────────────

    private void handleUpdateStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showWarn("Please select a complaint row first.");
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 0);
        if ("–".equals(idValue.toString())) return;

        int    complaintId = (int) idValue;
        String newStatus   = (String) statusCombo.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Update complaint #" + complaintId + " to \"" + newStatus + "\"?",
            "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = complaintDAO.updateStatus(complaintId, newStatus);
        if (success) {
            tableModel.setValueAt(newStatus, selectedRow, 4);
            JOptionPane.showMessageDialog(this,
                "Complaint #" + complaintId + " updated to \"" + newStatus + "\".",
                "Updated", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Update failed. Please try again.");
        }
    }

    // ── Delete complaint ───────────────────────────────────────────────────────

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showWarn("Please select a complaint row to delete.");
            return;
        }

        Object idVal = tableModel.getValueAt(selectedRow, 0);
        if ("–".equals(idVal.toString())) return;

        int    complaintId = (int) idVal;
        String title       = tableModel.getValueAt(selectedRow, 2).toString();

        // Double-confirm — deletion is permanent
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Permanently delete this complaint?<br><br>" +
            "<b>ID:</b> " + complaintId + "<br>" +
            "<b>Title:</b> " + title + "<br><br>" +
            "<i>This action cannot be undone.</i></html>",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = complaintDAO.deleteComplaint(complaintId);
        if (ok) {
            tableModel.removeRow(selectedRow);
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"–", "–", "No complaints in the system.", "", "", ""});
            }
            JOptionPane.showMessageDialog(this,
                "Complaint #" + complaintId + " deleted successfully.",
                "Deleted", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Delete failed. Please try again.");
        }
    }

    // ── Search ──────────────────────────────────────────────────────────────────

    private void handleSearch() {
        String input = searchField.getText().trim();
        if (input.isEmpty()) { loadAllComplaints(); return; }

        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric complaint ID.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Complaint c = complaintDAO.searchById(id);
        if (c == null) {
            JOptionPane.showMessageDialog(this, "No complaint found with ID: " + id,
                "Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{
            c.getId(),
            c.getUserName() != null ? c.getUserName() : "N/A",
            c.getTitle(),
            truncate(c.getDescription(), 45),
            c.getStatus(),
            c.getDate()
        });
    }

    // ── Logout ──────────────────────────────────────────────────────────────────

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(8, 4));
        table.setShowGrid(true);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(270);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);

        // Colour-coded Status column
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        // Alternating row colours
        table.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
                private final StatusCellRenderer sr = new StatusCellRenderer();
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val,
                        boolean sel, boolean focus, int row, int col) {
                    if (col == 4) return sr.getTableCellRendererComponent(t, val, sel, focus, row, col);
                    Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                    if (!sel) {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                        c.setForeground(Color.DARK_GRAY);
                    }
                    return c;
                }
            });
    }

    private String truncate(String text, int max) {
        return (text != null && text.length() > max) ? text.substring(0, max) + "…" : text;
    }

    private JLabel makeSmallLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    private JButton makeToolbarButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(105, 28));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JSeparator makeSep() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 24));
        return sep;
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}