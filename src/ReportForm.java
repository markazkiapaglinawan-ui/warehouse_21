import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportForm {
    private static final String OVERVIEW_CARD = "overview";
    private static final String DAILY_CARD = "daily";
    private static final String LOW_STOCK_CARD = "low-stock";
    private static final String INVENTORY_CARD = "inventory";
    private static final String ORDERS_CARD = "orders";
    private static final String SALES_CARD = "sales";

    private final UserRole userRole;
    private final CardLayout contentLayout = new CardLayout();

    private JPanel mainPanel;
    private JPanel contentPanel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;

    private JTable dailyTable;
    private JTable lowStockTable;
    private JTable inventoryTable;
    private JTable orderTable;
    private JTable salesTable;
    private JLabel salesIncomeLabel;

    public ReportForm() {
        this(UserRole.ADMIN);
    }

    public ReportForm(UserRole userRole) {
        this.userRole = userRole;
        buildUi();
        showOverview();
    }

    private void buildUi() {
        mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(ModuleTheme.PAGE_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Stock Monitoring & Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(ModuleTheme.TITLE_COLOR);

        descriptionLabel = new JLabel("Select a report option from the top menu.");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionLabel.setForeground(ModuleTheme.SUBTITLE_COLOR);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(descriptionLabel);

        contentPanel = new JPanel(contentLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(createOverviewPanel(), OVERVIEW_CARD);
        contentPanel.add(createDailyPanel(), DAILY_CARD);
        contentPanel.add(createLowStockPanel(), LOW_STOCK_CARD);
        contentPanel.add(createInventoryPanel(), INVENTORY_CARD);
        contentPanel.add(createOrdersPanel(), ORDERS_CARD);
        contentPanel.add(createSalesPanel(), SALES_CARD);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(16, 0));
        JLabel message = new JLabel("<html><div style='width:520px;'>Use the Reports dropdown in the top navigation bar to open Daily Inventory, Low Stock Alerts, Inventory Report, Order Report, or Sales Report in this same panel.</div></html>");
        message.setFont(new Font("Segoe UI", Font.BOLD, 22));
        message.setForeground(ModuleTheme.SURFACE_TEXT);
        JPanel textWrap = new JPanel(new BorderLayout());
        textWrap.setOpaque(false);
        textWrap.add(message, BorderLayout.NORTH);
        panel.add(textWrap, BorderLayout.WEST);
        panel.add(createOverviewIllustrationPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent createOverviewIllustrationPanel() {
        JComponent illustration = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int pad = 18;
                int frameW = w - pad * 2;
                int frameH = h - pad * 2;

                g2.setColor(new Color(22, 57, 104, 60));
                g2.fillRoundRect(pad, pad, frameW, frameH, 12, 12);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.drawRoundRect(pad, pad, frameW, frameH, 12, 12);

                int left = pad + 28;
                int top = pad + 24;
                int chartW = frameW - 56;
                int chartH = frameH - 56;

                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(left, top, chartW, chartH, 10, 10);
                g2.setColor(new Color(255, 255, 255, 90));
                g2.drawRoundRect(left, top, chartW, chartH, 10, 10);

                int axisLeft = left + 34;
                int axisBottom = top + chartH - 28;
                int axisRight = left + chartW - 20;
                int axisTop = top + 20;

                g2.drawLine(axisLeft, axisBottom, axisRight, axisBottom);
                g2.drawLine(axisLeft, axisBottom, axisLeft, axisTop);

                g2.setColor(new Color(123, 179, 255, 200));
                int[] bars = {68, 92, 54, 110, 80};
                int barW = 34;
                int gap = 22;
                int x = axisLeft + 24;
                for (int bar : bars) {
                    int y = axisBottom - bar;
                    g2.fillRoundRect(x, y, barW, bar, 6, 6);
                    x += barW + gap;
                }

                g2.setColor(new Color(122, 217, 194, 210));
                int[] ptsX = {axisLeft + 26, axisLeft + 90, axisLeft + 156, axisLeft + 224, axisLeft + 290};
                int[] ptsY = {axisBottom - 34, axisBottom - 62, axisBottom - 50, axisBottom - 86, axisBottom - 74};
                for (int i = 0; i < ptsX.length - 1; i++) {
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawLine(ptsX[i], ptsY[i], ptsX[i + 1], ptsY[i + 1]);
                }
                for (int i = 0; i < ptsX.length; i++) {
                    g2.fillOval(ptsX[i] - 4, ptsY[i] - 4, 8, 8);
                }
                g2.dispose();
            }
        };
        illustration.setOpaque(false);
        illustration.setPreferredSize(new Dimension(520, 280));
        return illustration;
    }

    private JPanel createDailyPanel() {
        dailyTable = createTable();
        return createTablePanel("Daily Inventory", dailyTable);
    }

    private JPanel createLowStockPanel() {
        lowStockTable = createTable();
        return createTablePanel("Low Stock Alerts", lowStockTable);
    }

    private JPanel createInventoryPanel() {
        inventoryTable = createTable();
        return createTablePanel("Inventory Report", inventoryTable);
    }

    private JPanel createOrdersPanel() {
        orderTable = createTable();
        return createTablePanel("Order Report", orderTable);
    }

    private JPanel createSalesPanel() {
        salesTable = createTable();
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));
        panel.add(createSectionTitle("Recent Sales Activity"), BorderLayout.NORTH);
        panel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        salesIncomeLabel = new JLabel("Overall Warehouse Income: PHP 0.00");
        salesIncomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        salesIncomeLabel.setForeground(ModuleTheme.SURFACE_TEXT);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(salesIncomeLabel);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTablePanel(String sectionTitle, JTable table) {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));
        panel.add(createSectionTitle(sectionTitle), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JTable createTable() {
        JTable table = new JTable();
        table.setModel(new DefaultTableModel(new Object[][]{}, new Object[]{}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        return table;
    }

    private JPanel createSurfacePanel() {
        return ModuleTheme.createSurfacePanel();
    }

    private JLabel createSectionTitle(String text) {
        return ModuleTheme.createSectionTitle(text);
    }

    private void showOverview() {
        descriptionLabel.setText("Select a report option from the top menu.");
        contentLayout.show(contentPanel, OVERVIEW_CARD);
    }

    private void showDailyReport() {
        descriptionLabel.setText("View daily inventory status in this panel.");
        List<DataStorage.Item> items = DataStorage.getInstance().getItems();
        Object[][] data = new Object[items.size()][4];
        for (int i = 0; i < items.size(); i++) {
            DataStorage.Item item = items.get(i);
            data[i] = new Object[]{item.id, item.name, item.quantity, item.quantity > 5 ? "IN STOCK" : "LOW STOCK"};
        }
        setTableData(dailyTable, data, new Object[]{"Item ID", "Item Name", "Quantity", "Status"});
        contentLayout.show(contentPanel, DAILY_CARD);
    }

    private void showLowStockReport() {
        descriptionLabel.setText("View low stock alerts in this panel.");
        List<DataStorage.Item> allItems = DataStorage.getInstance().getItems();
        List<DataStorage.Item> lowStock = new ArrayList<>();
        for (DataStorage.Item item : allItems) {
            if (item.quantity <= 5) {
                lowStock.add(item);
            }
        }
        Object[][] data = new Object[lowStock.size()][4];
        for (int i = 0; i < lowStock.size(); i++) {
            DataStorage.Item item = lowStock.get(i);
            data[i] = new Object[]{item.id, item.name, item.quantity, "REORDER REQUIRED"};
        }
        setTableData(lowStockTable, data, new Object[]{"Item ID", "Item Name", "Current Qty", "Alert Level"});
        contentLayout.show(contentPanel, LOW_STOCK_CARD);
    }

    private void showInventoryReport() {
        descriptionLabel.setText("View inventory report in this panel.");
        List<DataStorage.Item> items = DataStorage.getInstance().getItems();
        Object[][] data = new Object[items.size()][4];
        for (int i = 0; i < items.size(); i++) {
            DataStorage.Item item = items.get(i);
            data[i] = new Object[]{item.id, item.category, formatCurrency(item.price), item.quantity};
        }
        setTableData(inventoryTable, data, new Object[]{"Item ID", "Category", "Price", "Stock"});
        contentLayout.show(contentPanel, INVENTORY_CARD);
    }

    private void showOrderReport() {
        descriptionLabel.setText("View order report in this panel.");
        List<DataStorage.Order> orders = DataStorage.getInstance().getOrders();
        Object[][] data = new Object[orders.size()][6];
        for (int i = 0; i < orders.size(); i++) {
            DataStorage.Order order = orders.get(i);
            String paymentStatus;
            if (order.paid) {
                String method = (order.paymentMethod == null || order.paymentMethod.isBlank())
                        ? "CASH"
                        : order.paymentMethod.toUpperCase();
                paymentStatus = "PAID - " + method;
            } else {
                paymentStatus = "UNPAID";
            }
            data[i] = new Object[]{order.id, order.customer, order.itemName, order.status, paymentStatus, order.date};
        }
        setTableData(orderTable, data, new Object[]{"Order ID", "Customer", "Item Name", "Status", "Payment", "Date"});
        contentLayout.show(contentPanel, ORDERS_CARD);
    }

    private void showSalesReport() {
        descriptionLabel.setText("View sales activity in this panel.");
        List<DataStorage.Order> orders = DataStorage.getInstance().getOrders();
        List<DataStorage.Item> items = DataStorage.getInstance().getItems();
        Map<String, Double> itemPriceByName = new HashMap<>();
        for (DataStorage.Item item : items) {
            itemPriceByName.put(item.name.toLowerCase(), item.price);
        }

        Object[][] data = new Object[orders.size()][5];
        double totalIncome = 0.0;
        for (int i = 0; i < orders.size(); i++) {
            DataStorage.Order order = orders.get(i);
            double unitPrice = itemPriceByName.getOrDefault(order.itemName.toLowerCase(), 0.0);
            double lineTotal = unitPrice * order.quantity;
            if (order.paid) {
                totalIncome += lineTotal;
            }
            data[i] = new Object[]{order.date, order.customer, order.itemName, order.quantity, formatCurrency(unitPrice)};
        }
        setTableData(salesTable, data, new Object[]{"Date", "Customer", "Product", "Quantity", "Price"});
        salesIncomeLabel.setText("Overall Warehouse Income: " + formatCurrency(totalIncome));
        contentLayout.show(contentPanel, SALES_CARD);
    }

    private void setTableData(JTable table, Object[][] data, Object[] columns) {
        table.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private String formatCurrency(double amount) {
        return String.format("PHP %.2f", amount);
    }

    public void showAction(String actionKey) {
        switch (actionKey) {
            case "daily" -> showDailyReport();
            case "low-stock" -> showLowStockReport();
            case "inventory" -> showInventoryReport();
            case "orders" -> showOrderReport();
            case "sales" -> showSalesReport();
            default -> throw new IllegalArgumentException("Unknown report action: " + actionKey);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
