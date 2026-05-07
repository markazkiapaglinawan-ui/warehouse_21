import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerServiceForm {
    private final JPanel mainPanel;
    private final JTable messageTable;
    private final JTextArea detailArea;
    private List<DataStorage.CustomerServiceMessage> messages = new ArrayList<>();

    public CustomerServiceForm() {
        mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setBackground(new Color(245, 246, 248));
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JLabel title = new JLabel("Customer Service Messages");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(28, 41, 64));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(new Color(25, 118, 210));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBorder(new EmptyBorder(8, 14, 8, 14));
        refreshButton.addActionListener(event -> refreshMessages());

        header.add(title, BorderLayout.WEST);
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

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(messageTable),
                new JScrollPane(detailArea)
        );
        splitPane.setResizeWeight(0.58);
        splitPane.setBorder(BorderFactory.createLineBorder(new Color(205, 212, 222)));
        mainPanel.add(splitPane, BorderLayout.CENTER);

        refreshMessages();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void refreshMessages() {
        try {
            messages = DataStorage.getInstance().getCustomerServiceMessages();
            DefaultTableModel model = new DefaultTableModel(
                    new Object[]{"Date", "Customer", "Username", "Subject", "Order ID"}, 0
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
                        message.orderCode.isBlank() ? "-" : message.orderCode
                });
            }

            messageTable.setModel(model);
            if (messages.isEmpty()) {
                detailArea.setText("No customer service messages yet.");
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
                message.message
        );
        detailArea.setCaretPosition(0);
    }
}
