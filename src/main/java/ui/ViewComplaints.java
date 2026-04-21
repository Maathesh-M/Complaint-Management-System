package ui;
import dao.ComplaintDAO;
import model.Complaint;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
public class ViewComplaints extends JFrame {
    private JTable             table;
    private DefaultTableModel  tableModel;
    private JButton            refreshButton;
    private JButton            closeButton;
    private JTextField         searchField;
    private JButton            searchButton;
    private final User         currentUser;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private static final String[] COLUMNS = {"ID", "Title", "Description", "Status", "Date"};
    public ViewComplaints(User user) {
        this.currentUser = user;
        initComponents();
        loadComplaints();
    }
    private void initComponents() {
        setTitle("My Complaints – " + currentUser.getName());
        setSize(750, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(245, 247, 250));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel titleLabel = new JLabel("My Complaints");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(new Color(245, 247, 250));
        searchPanel.add(new JLabel("Search by ID:"));
        searchField = new JTextField(10);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(searchField);
        searchButton = makeButton("Search", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        JButton resetButton = makeButton("Show All", new Color(149, 165, 166));
        searchPanel.add(resetButton);
        mainPanel.add(searchPanel, BorderLayout.BEFORE_FIRST_LINE); 
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(header, BorderLayout.NORTH);
        topWrapper.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(topWrapper, BorderLayout.NORTH);
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 12, 0, 12));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttonPanel.setBackground(new Color(245, 247, 250));
        refreshButton = makeButton("Refresh", new Color(39, 174, 96));
        closeButton   = makeButton("Close", new Color(149, 165, 166));
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Listeners
        refreshButton.addActionListener(e -> loadComplaints());
        closeButton.addActionListener(e -> dispose());
        searchButton.addActionListener(e -> handleSearch());
        resetButton.addActionListener(e -> { searchField.setText(""); loadComplaints(); });
    }
    private void loadComplaints() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Complaint> complaints = complaintDAO.getComplaintsByUser(currentUser.getId());

        if (complaints.isEmpty()) {
            // Show placeholder message
            tableModel.addRow(new Object[]{"–", "No complaints found.", "", "", ""});
            return;
        }

        for (Complaint c : complaints) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getTitle(),
                truncate(c.getDescription(), 50),
                c.getStatus(),
                c.getDate()
            });
        }
    }

    private void handleSearch() {
        String input = searchField.getText().trim();
        if (input.isEmpty()) { loadComplaints(); return; }

        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Complaint c = complaintDAO.searchById(id);
        if (c == null || c.getUserId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "No complaint found with ID: " + id,
                "Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{
            c.getId(), c.getTitle(), truncate(c.getDescription(), 50),
            c.getStatus(), c.getDate()
        });
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setIntercellSpacing(new Dimension(8, 4));
        table.setShowGrid(true);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(160);  // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(280);  // Description
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(140);  // Date

        // Colour-code the Status column
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
    }
    private String truncate(String text, int maxLen) {
        return (text != null && text.length() > maxLen)
            ? text.substring(0, maxLen) + "…"
            : text;
    }
    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Status colour renderer ────────────────────────────────────────────────
    static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(CENTER);
            if (!isSelected) {
                String status = value != null ? value.toString() : "";
                switch (status) {
                    case "Pending":     setBackground(new Color(253, 234, 200)); setForeground(new Color(180, 100, 0)); break;
                    case "In Progress": setBackground(new Color(209, 236, 241)); setForeground(new Color(30, 110, 160)); break;
                    case "Resolved":    setBackground(new Color(212, 239, 223)); setForeground(new Color(30, 130, 76)); break;
                    default:            setBackground(Color.WHITE); setForeground(Color.BLACK);
                }
            }
            return this;
        }
    }
}