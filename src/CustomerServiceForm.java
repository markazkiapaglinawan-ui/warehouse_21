import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomerServiceForm {
    private static final String CUSTOMER_SERVICE_LOGO = "assets/customer-service-logo.png";

    private final JPanel mainPanel;
    private final JTable messageTable;
    private final JTextArea detailArea;
    private final JTextArea replyArea;
    private final JLabel replyStatusLabel;
    private final String responderName;
    private List<DataStorage.CustomerServiceMessage> messages = new ArrayList<>();

    public CustomerServiceForm() {
        this(null);
    }

    public CustomerServiceForm(DataStorage.User responder) {
        responderName = responder == null
                ? "Staff/Admin"
                : (responder.fullName == null || responder.fullName.isBlank() ? responder.username : responder.fullName);
        mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBackground(new Color(245, 246, 248));
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(new Color(25, 118, 210));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBorder(new EmptyBorder(8, 14, 8, 14));
        refreshButton.addActionListener(event -> refreshMessages());

        header.add(createHeaderTitlePanel(), BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        messageTable = new JTable();
        messageTable.setRowHeight(26);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedMessage();
            }
        });

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailArea.setBackground(Color.WHITE);

        JPanel replyPanel = new JPanel(new BorderLayout(0, 8));
        replyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        replyPanel.setBackground(new Color(248, 250, 252));

        JLabel replyTitle = new JLabel("Reply to Customer");
        replyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        replyTitle.setForeground(new Color(28, 41, 64));
        replyPanel.add(replyTitle, BorderLayout.NORTH);

        replyArea = new JTextArea(5, 20);
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        replyArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        replyArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        replyPanel.add(new JScrollPane(replyArea), BorderLayout.CENTER);

        JButton sendReplyButton = new JButton("Send Reply");
        sendReplyButton.setFocusPainted(false);
        sendReplyButton.setBackground(new Color(25, 118, 210));
        sendReplyButton.setForeground(Color.WHITE);
        sendReplyButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendReplyButton.setBorder(new EmptyBorder(8, 14, 8, 14));
        sendReplyButton.addActionListener(event -> sendReply());

        replyStatusLabel = new JLabel(" ");
        replyStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel replyActions = new JPanel(new BorderLayout());
        replyActions.setOpaque(false);
        replyActions.add(replyStatusLabel, BorderLayout.WEST);
        replyActions.add(sendReplyButton, BorderLayout.EAST);
        replyPanel.add(replyActions, BorderLayout.SOUTH);

        JPanel lowerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        lowerPanel.setOpaque(false);
        lowerPanel.add(new JScrollPane(detailArea));
        lowerPanel.add(replyPanel);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(messageTable),
                lowerPanel
        );
        splitPane.setResizeWeight(0.58);
        splitPane.setBorder(BorderFactory.createLineBorder(new Color(205, 212, 222)));
        mainPanel.add(splitPane, BorderLayout.CENTER);

        refreshMessages();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel createHeaderTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setOpaque(false);

        JLabel logo = new JLabel(loadLocalImageIcon(CUSTOMER_SERVICE_LOGO, 130, 130));
        logo.setPreferredSize(new Dimension(130, 130));
        logo.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel title = new JLabel("Customer Service Messages");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(28, 41, 64));

        panel.add(logo, BorderLayout.WEST);
        panel.add(title, BorderLayout.CENTER);
        return panel;
    }

    private Icon loadLocalImageIcon(String imagePath, int maxWidth, int maxHeight) {
        try {
            BufferedImage image = javax.imageio.ImageIO.read(new File(imagePath));
            if (image == null) {
                return null;
            }
            double scale = Math.min((double) maxWidth / image.getWidth(), (double) maxHeight / image.getHeight());
            int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(image.getHeight() * scale));
            Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void refreshMessages() {
        try {
            messages = DataStorage.getInstance().getCustomerServiceMessages();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Date", "Customer", "Username", "Subject", "Order ID", "Reply Status"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (DataStorage.CustomerServiceMessage message : messages) {
                model.addRow(new Object[]{
                        message.createdAt,
                        message.customerName,
                        message.customerUsername,
                        message.subject,
                        message.orderCode.isBlank() ? "-" : message.orderCode,
                        message.reply.isBlank() ? "Pending" : "Replied"
                });
            }

            messageTable.setModel(model);
            if (messages.isEmpty()) {
                detailArea.setText("No customer service messages yet.");
                replyArea.setText("");
                replyStatusLabel.setText(" ");
            } else {
                messageTable.setRowSelectionInterval(0, 0);
                showSelectedMessage();
            }
        } catch (Exception exception) {
            detailArea.setText(exception.getMessage());
        }
    }

    private void showSelectedMessage() {
        int row = messageTable.getSelectedRow();
        if (row < 0 || row >= messages.size()) {
            return;
        }

        DataStorage.CustomerServiceMessage message = messages.get(row);
        detailArea.setText(
                "Date: " + message.createdAt + "\n" +
                "Customer: " + message.customerName + "\n" +
                "Username: " + message.customerUsername + "\n" +
                "Subject: " + message.subject + "\n" +
                "Order ID: " + (message.orderCode.isBlank() ? "-" : message.orderCode) + "\n\n" +
                "Concern:\n" + message.message + "\n\n" +
                "Reply:\n" + (message.reply.isBlank()
                        ? "No reply sent yet."
                        : message.reply + "\n\nReplied by: " + message.repliedBy + "\nReplied at: " + message.repliedAt)
        );
        replyArea.setText(message.reply);
        replyStatusLabel.setForeground(message.reply.isBlank() ? new Color(194, 65, 12) : new Color(46, 125, 50));
        replyStatusLabel.setText(message.reply.isBlank() ? "Pending reply." : "Reply already sent. Editing will update it.");
        detailArea.setCaretPosition(0);
    }

    private void sendReply() {
        int row = messageTable.getSelectedRow();
        if (row < 0 || row >= messages.size()) {
            replyStatusLabel.setForeground(new Color(211, 47, 47));
            replyStatusLabel.setText("Select a customer concern first.");
            return;
        }

        String reply = replyArea.getText().trim();
        if (reply.isEmpty()) {
            replyStatusLabel.setForeground(new Color(211, 47, 47));
            replyStatusLabel.setText("Reply message is required.");
            return;
        }

        DataStorage.CustomerServiceMessage message = messages.get(row);
        try {
            DataStorage.getInstance().replyToCustomerServiceMessage(message.id, reply, responderName);
            replyStatusLabel.setForeground(new Color(46, 125, 50));
            replyStatusLabel.setText("Reply sent to customer.");
            refreshMessages();
        } catch (Exception exception) {
            replyStatusLabel.setForeground(new Color(211, 47, 47));
            replyStatusLabel.setText(exception.getMessage());
        }
    }
}
