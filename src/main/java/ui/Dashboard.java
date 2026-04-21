package ui;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public class Dashboard extends JFrame {
    private final User currentUser;
    public Dashboard(User user) {
        this.currentUser = user;
        initComponents();
    }
    private void initComponents() {
        setTitle("Dashboard – " + currentUser.getName());
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel appTitle = new JLabel("Complaint Management System");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appTitle.setForeground(Color.WHITE);

        JLabel userInfo = new JLabel("Logged in: " + currentUser.getName() + "  [" + currentUser.getRole() + "]");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(new Color(189, 195, 199));

        header.add(appTitle, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(245, 247, 250));
        welcomePanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(52, 73, 94));
        welcomePanel.add(welcomeLabel);
        mainPanel.add(welcomePanel, BorderLayout.AFTER_LAST_LINE); 
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardPanel.setBackground(new Color(245, 247, 250));
        cardPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        cardPanel.add(createMenuCard(
            "File Complaint", "Submit a new complaint",
            new Color(231, 76, 60), e -> new ComplaintForm(currentUser).setVisible(true)
        ));
        cardPanel.add(createMenuCard(
            "View Complaints", "See your complaints",
            new Color(41, 128, 185), e -> new ViewComplaints(currentUser).setVisible(true)
        ));
        cardPanel.add(createMenuCard(
            "Logout", "Sign out of the system",
            new Color(149, 165, 166), e -> handleLogout()
        ));
        JPanel centreWrapper = new JPanel(new BorderLayout());
        centreWrapper.setBackground(new Color(245, 247, 250));
        centreWrapper.add(welcomePanel, BorderLayout.NORTH);
        centreWrapper.add(cardPanel, BorderLayout.CENTER);
        JPanel statsPanel = buildStatsStrip();
        centreWrapper.add(statsPanel, BorderLayout.SOUTH);
        mainPanel.add(centreWrapper, BorderLayout.CENTER);
        JPanel footer = new JPanel();
        footer.setBackground(new Color(236, 240, 241));
        footer.setBorder(new EmptyBorder(6, 0, 6, 0));
        JLabel footerLabel = new JLabel("© 2026 Complaint Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(Color.GRAY);
        footer.add(footerLabel);
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }
    private JPanel createMenuCard(String title, String subtitle, Color color,
                                   java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(16, 12, 16, 12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel(subtitle, SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(new Color(255, 255, 255, 200));

        card.add(titleLbl, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(color.darker());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(color);
            }
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
        });
        return card;
    }
    private JPanel buildStatsStrip() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        strip.setBackground(new Color(245, 247, 250));
        JLabel hint = new JLabel("Click a card above to get started.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);
        strip.add(hint);
        return strip;
    }
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}