import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReceiverForm {
    private static final String QUEUE_VIEW = "queue";
    private static final String HISTORY_VIEW = "history";
    private static final String[] COURIERS = {"DHL", "FedEx", "LBC", "J&T", "ZendEase"};

    private final JPanel mainPanel;
    private final CardLayout viewLayout;
    private final JPanel viewPanel;

    private final DefaultListModel<DataStorage.Order> queueListModel = new DefaultListModel<>();
    private final JList<DataStorage.Order> queueList = new JList<>(queueListModel);
    private final JLabel selectedOrderTitle = new JLabel("No order selected");
    private final JTextArea selectedOrderDetails = new JTextArea();
    private final JList<String> courierList = new JList<>(COURIERS);
    private final JLabel statusLabel = new JLabel(" ");
    private final JTable historyTable = new JTable();
    private final JComboBox<String> historyCourierBox = new JComboBox<>(COURIERS);

    public ReceiverForm() {
        mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(ModuleTheme.PAGE_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Receiver Dispatch Panel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(ModuleTheme.TITLE_COLOR);
        JLabel subtitle = new JLabel("Open a customer order, choose courier, and assign for delivery.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(ModuleTheme.SUBTITLE_COLOR);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        viewLayout = new CardLayout();
        viewPanel = new JPanel(viewLayout);
        viewPanel.setOpaque(false);
        viewPanel.add(buildQueueView(), QUEUE_VIEW);
        viewPanel.add(buildHistoryView(), HISTORY_VIEW);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(viewPanel, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        showQueueView();
    }

    private JPanel buildQueueView() {
        JPanel root = ModuleTheme.createSurfacePanel();
        root.setLayout(new BorderLayout(12, 12));

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topActions.setOpaque(false);
        JButton queueButton = new JButton("Order Queue");
        JButton historyButton = new JButton("History");
        JButton refreshButton = new JButton("Refresh");
        ModulePanelFactory.styleActionButton(queueButton, new Color(25, 118, 210));
        ModulePanelFactory.styleActionButton(historyButton, new Color(67, 56, 202));
        ModulePanelFactory.styleActionButton(refreshButton, new Color(92, 107, 128));
        queueButton.addActionListener(e -> showQueueView());
        historyButton.addActionListener(e -> showHistoryView());
        refreshButton.addActionListener(e -> loadQueueOrders());
        topActions.add(queueButton);
        topActions.add(historyButton);
        topActions.add(refreshButton);
        root.add(topActions, BorderLayout.NORTH);

        queueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queueList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        queueList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value.id + " | " + value.customer + " | " + value.itemName + " x" + value.quantity);
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(8, 10, 8, 10));
            label.setBackground(isSelected ? new Color(219, 234, 254) : Color.WHITE);
            label.setForeground(new Color(15, 23, 42));
            return label;
        });
        queueList.addListSelectionListener(e -> showSelectedOrder());

        JScrollPane queueScroll = new JScrollPane(queueList);
        queueScroll.setPreferredSize(new Dimension(460, 0));

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 8, 0, 0));

        selectedOrderTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        selectedOrderTitle.setForeground(Color.WHITE);

        selectedOrderDetails.setEditable(false);
        selectedOrderDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectedOrderDetails.setLineWrap(true);
        selectedOrderDetails.setWrapStyleWord(true);
        selectedOrderDetails.setBackground(new Color(248, 250, 252));
        selectedOrderDetails.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel courierPanel = new JPanel(new BorderLayout(0, 8));
        courierPanel.setOpaque(false);
        JLabel courierLabel = new JLabel("Choose Courier");
        courierLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        courierLabel.setForeground(Color.WHITE);
        courierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courierList.setVisibleRowCount(5);
        courierList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courierList.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        courierPanel.add(courierLabel, BorderLayout.NORTH);
        courierPanel.add(new JScrollPane(courierList), BorderLayout.CENTER);

        JButton assignButton = new JButton("Assign Courier");
        ModulePanelFactory.styleActionButton(assignButton, new Color(0, 150, 136));
        assignButton.addActionListener(e -> assignCourierToSelectedOrder());

        rightPanel.add(selectedOrderTitle, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(selectedOrderDetails), BorderLayout.CENTER);
        rightPanel.add(courierPanel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setOpaque(false);
        content.add(queueScroll, BorderLayout.WEST);
        content.add(rightPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(assignButton);

        root.add(content, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHistoryView() {
        JPanel panel = ModuleTheme.createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 12));

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topActions.setOpaque(false);
        JButton queueButton = new JButton("Order Queue");
        JButton historyButton = new JButton("History");
        JButton refreshButton = new JButton("Refresh");
        JButton updateCourierButton = new JButton("Update Courier");
        ModulePanelFactory.styleActionButton(queueButton, new Color(25, 118, 210));
        ModulePanelFactory.styleActionButton(historyButton, new Color(67, 56, 202));
        ModulePanelFactory.styleActionButton(refreshButton, new Color(92, 107, 128));
        ModulePanelFactory.styleActionButton(updateCourierButton, new Color(0, 150, 136));
        queueButton.addActionListener(e -> showQueueView());
        historyButton.addActionListener(e -> showHistoryView());
        refreshButton.addActionListener(e -> loadHistory());
        updateCourierButton.addActionListener(e -> updateCourierFromHistory());
        topActions.add(queueButton);
        topActions.add(historyButton);
        topActions.add(refreshButton);
        topActions.add(new JLabel("Set Courier"));
        topActions.add(historyCourierBox);
        topActions.add(updateCourierButton);

        historyTable.setRowHeight(28);
        historyTable.setFillsViewportHeight(true);
        historyTable.setModel(new DefaultTableModel(new Object[][]{}, new Object[]{"Order ID", "Customer", "Item", "Qty", "Payment", "Courier", "Status", "Order Date", "Forwarded At"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        panel.add(topActions, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadQueueOrders() {
        queueListModel.clear();
        List<DataStorage.Order> orders = DataStorage.getInstance().getOrders();
        for (DataStorage.Order order : orders) {
            if (order.courierName == null || order.courierName.isBlank()) {
                queueListModel.addElement(order);
            }
        }
        if (!queueListModel.isEmpty()) {
            queueList.setSelectedIndex(0);
        } else {
            selectedOrderTitle.setText("No order selected");
            selectedOrderDetails.setText("No pending orders.");
        }
        showStatus("Loaded " + queueListModel.size() + " pending order(s).", new Color(46, 125, 50));
    }

    private void showSelectedOrder() {
        DataStorage.Order selected = queueList.getSelectedValue();
        if (selected == null) {
            selectedOrderTitle.setText("No order selected");
            selectedOrderDetails.setText("");
            return;
        }
        selectedOrderTitle.setText("Order " + selected.id);
        selectedOrderDetails.setText(
                "Customer: " + selected.customer + "\n" +
                "Item: " + selected.itemName + "\n" +
                "Quantity: " + selected.quantity + "\n" +
                "Payment: " + (selected.paymentMethod == null || selected.paymentMethod.isBlank() ? "-" : selected.paymentMethod) + "\n" +
                "Status: " + selected.status + "\n" +
                "Order Date: " + selected.date
        );
    }

    private void assignCourierToSelectedOrder() {
        DataStorage.Order selectedOrder = queueList.getSelectedValue();
        if (selectedOrder == null) {
            showStatus("Select an order first.", new Color(211, 47, 47));
            return;
        }
        String courier = courierList.getSelectedValue();
        if (courier == null || courier.isBlank()) {
            showStatus("Select a courier from the list first.", new Color(211, 47, 47));
            return;
        }
        try {
            DataStorage.getInstance().assignCourier(selectedOrder.id, courier);
            loadQueueOrders();
            loadHistory();
            showStatus("Order " + selectedOrder.id + " assigned to " + courier + ".", new Color(46, 125, 50));
        } catch (Exception ex) {
            showStatus(ex.getMessage(), new Color(211, 47, 47));
        }
    }

    private void loadHistory() {
        List<DataStorage.Order> orders = DataStorage.getInstance().getOrders();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Order ID", "Customer", "Item", "Qty", "Payment", "Courier", "Status", "Order Date", "Forwarded At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (DataStorage.Order order : orders) {
            if (order.courierName == null || order.courierName.isBlank()) {
                continue;
            }
            model.addRow(new Object[]{
                    order.id,
                    order.customer,
                    order.itemName,
                    order.quantity,
                    order.paymentMethod == null || order.paymentMethod.isBlank() ? "-" : order.paymentMethod,
                    order.courierName,
                    order.status,
                    order.date,
                    order.forwardedAt == null || order.forwardedAt.isBlank() ? "-" : order.forwardedAt
            });
        }
        historyTable.setModel(model);
    }

    private void updateCourierFromHistory() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            showStatus("Select a history order first.", new Color(211, 47, 47));
            return;
        }
        String orderId = String.valueOf(historyTable.getValueAt(row, 0));
        String courier = (String) historyCourierBox.getSelectedItem();
        if (courier == null || courier.isBlank()) {
            showStatus("Select a courier.", new Color(211, 47, 47));
            return;
        }
        try {
            DataStorage.getInstance().assignCourier(orderId, courier);
            loadHistory();
            showStatus("Order " + orderId + " courier updated to " + courier + ".", new Color(46, 125, 50));
        } catch (Exception ex) {
            showStatus(ex.getMessage(), new Color(211, 47, 47));
        }
    }

    public void showQueueView() {
        loadQueueOrders();
        viewLayout.show(viewPanel, QUEUE_VIEW);
    }

    public void showHistoryView() {
        loadHistory();
        viewLayout.show(viewPanel, HISTORY_VIEW);
        showStatus("Showing receiver history.", new Color(46, 125, 50));
    }

    private void showStatus(String message, Color color) {
        statusLabel.setForeground(color);
        statusLabel.setText(message);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
