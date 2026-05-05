import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerMainMenu {
    private static final String SHOP_VIEW = "shop";
    private static final String CART_VIEW = "cart";
    private static final String PAYMENT_VIEW = "payment";

    private final DataStorage.User currentUser;
    private final Runnable logoutHandler;
    private final Map<Product, Integer> cart = new LinkedHashMap<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private final List<Product> products = new ArrayList<>();
    private final CardLayout contentLayout = new CardLayout();

    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel cardsPanel;
    private JPanel cartItemsPanel;
    private JLabel cartBadgeLabel;
    private JLabel userLabel;
    private JComboBox<String> categoryBox;
    private JTextField searchField;

    private JTextField paymentNameField;
    private JTextField paymentAddressField;
    private JTextField paymentPhoneField;
    private JComboBox<String> paymentOptionBox;
    private JLabel paymentSummaryLabel;
    private JLabel paymentDeliveryLabel;
    private Map<Product, Integer> paymentItems = new LinkedHashMap<>();
    private boolean paymentIsPreOrder;

    public CustomerMainMenu(DataStorage.User currentUser, Runnable logoutHandler) {
        this.currentUser = currentUser;
        this.logoutHandler = logoutHandler;
        seedProducts();
        buildUi();
    }

    private void buildUi() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(98, 140, 224));
        mainPanel.add(buildHeader(), BorderLayout.NORTH);

        contentPanel = new JPanel(contentLayout);
        contentPanel.add(buildShopPanel(), SHOP_VIEW);
        contentPanel.add(buildCartPanel(), CART_VIEW);
        contentPanel.add(buildPaymentPanel(), PAYMENT_VIEW);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        showView(SHOP_VIEW);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBorder(new EmptyBorder(10, 16, 10, 16));
        header.setBackground(new Color(41, 49, 63));

        JLabel brand = new JLabel("Warehouse Shop");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        categoryBox = new JComboBox<>(new String[]{"All", "Accessories", "Office", "Cables", "Storage", "Audio", "Networking"});
        searchField = new JTextField(16);
        JButton searchButton = new JButton("Search");
        styleHeaderButton(searchButton, new Color(25, 135, 84));
        searchButton.addActionListener(e -> refreshProductCards());
        categoryBox.addActionListener(e -> refreshProductCards());
        left.add(brand);
        left.add(categoryBox);
        left.add(searchField);
        left.add(searchButton);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        userLabel = new JLabel(currentUser.fullName.isBlank() ? currentUser.username : currentUser.fullName);
        userLabel.setForeground(new Color(220, 230, 244));
        userLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openProfileDialog();
            }
        });

        cartBadgeLabel = new JLabel("Cart (0)");
        cartBadgeLabel.setForeground(Color.WHITE);
        JButton cartButton = new JButton("View Cart");
        JButton profileButton = new JButton("Profile");
        JButton logoutButton = new JButton("Logout");
        styleHeaderButton(cartButton, new Color(37, 99, 235));
        styleHeaderButton(profileButton, new Color(108, 117, 125));
        styleHeaderButton(logoutButton, new Color(220, 53, 69));
        cartButton.addActionListener(e -> {
            refreshCartPanel();
            showView(CART_VIEW);
        });
        profileButton.addActionListener(e -> openProfileDialog());
        logoutButton.addActionListener(e -> {
            if (logoutHandler != null) {
                logoutHandler.run();
            }
        });

        right.add(userLabel);
        right.add(cartBadgeLabel);
        right.add(cartButton);
        right.add(profileButton);
        right.add(logoutButton);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildShopPanel() {
        JPanel shopPanel = new JPanel(new BorderLayout());
        shopPanel.setOpaque(false);

        String displayName = currentUser.fullName.isBlank() ? currentUser.username : currentUser.fullName;
        JLabel title = new JLabel(displayName + ", welcome to RAMJA'S Warehouse");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(new Color(18, 56, 124));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(24, 0, 18, 0));
        shopPanel.add(title, BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(0, 4, 14, 14));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(12, 28, 18, 28));
        refreshProductCards();

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        shopPanel.add(scrollPane, BorderLayout.CENTER);
        return shopPanel;
    }

    private JPanel buildCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout(0, 12));
        cartPanel.setOpaque(false);
        cartPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Your Cart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(new Color(18, 56, 124));
        cartPanel.add(title, BorderLayout.NORTH);

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        JButton continueShopping = new JButton("Continue Shopping");
        JButton checkout = new JButton("Proceed to Payment");
        styleActionButton(continueShopping, new Color(108, 117, 125));
        styleActionButton(checkout, new Color(46, 125, 50));
        continueShopping.addActionListener(e -> showView(SHOP_VIEW));
        checkout.addActionListener(e -> {
            Map<Product, Integer> items = getNonZeroCartItems();
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Cart is empty.", "Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openPaymentPanel(items, false);
        });
        footer.add(continueShopping);
        footer.add(checkout);
        cartPanel.add(footer, BorderLayout.SOUTH);
        return cartPanel;
    }

    private JPanel buildPaymentPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setPreferredSize(new Dimension(520, 520));
        panel.setBackground(new Color(245, 246, 248));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 212, 222)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Payment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(title);

        paymentSummaryLabel = new JLabel();
        paymentDeliveryLabel = new JLabel();
        panel.add(paymentSummaryLabel);
        panel.add(paymentDeliveryLabel);

        panel.add(new JLabel("Name"));
        paymentNameField = new JTextField(currentUser.fullName);
        panel.add(paymentNameField);

        panel.add(new JLabel("Address"));
        paymentAddressField = new JTextField(currentUser.address);
        panel.add(paymentAddressField);

        panel.add(new JLabel("Phone Number"));
        paymentPhoneField = new JTextField(currentUser.phoneNumber);
        panel.add(paymentPhoneField);

        panel.add(new JLabel("Payment Option"));
        paymentOptionBox = new JComboBox<>(new String[]{"QR Code", "Cash"});
        panel.add(paymentOptionBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton backButton = new JButton("Back");
        JButton checkoutButton = new JButton("Check Out");
        styleActionButton(backButton, new Color(108, 117, 125));
        styleActionButton(checkoutButton, new Color(25, 118, 210));
        backButton.addActionListener(e -> showView(CART_VIEW));
        checkoutButton.addActionListener(e -> confirmCheckout());
        buttons.add(backButton);
        buttons.add(checkoutButton);
        panel.add(new JLabel());
        panel.add(buttons);

        wrapper.add(panel);
        return wrapper;
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(245, 246, 248));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 212, 222)),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel image = new JLabel(loadProductImage(product.imageUrl), SwingConstants.CENTER);
        image.setPreferredSize(new Dimension(210, 130));
        card.add(image, BorderLayout.NORTH);

        JPanel meta = new JPanel(new GridLayout(0, 1));
        meta.setOpaque(false);
        meta.add(label(product.name, Font.BOLD, 16, new Color(33, 37, 41)));
        meta.add(label(currencyFormat.format(product.price), Font.PLAIN, 14, new Color(33, 37, 41)));
        meta.add(label("Stock: " + product.stock, Font.PLAIN, 13, new Color(73, 80, 87)));
        card.add(meta, BorderLayout.CENTER);

        JTextField qtyField = new JTextField("1");
        qtyField.setHorizontalAlignment(SwingConstants.CENTER);
        qtyField.setPreferredSize(new Dimension(44, 28));

        JButton minus = new JButton("-");
        JButton plus = new JButton("+");
        styleQuantityButton(minus);
        styleQuantityButton(plus);
        plus.setText("+");
        minus.addActionListener(e -> {
            int qty = readQuantity(qtyField);
            if (qty > 1) {
                qtyField.setText(String.valueOf(qty - 1));
            }
        });
        plus.addActionListener(e -> qtyField.setText(String.valueOf(readQuantity(qtyField) + 1)));

        JButton addToCart = new JButton("Add to Cart");
        JButton buyNow = new JButton(product.stock > 0 ? "Buy Now" : "Pre-Order");
        styleActionButton(addToCart, new Color(25, 118, 210));
        styleActionButton(buyNow, product.stock > 0 ? new Color(46, 125, 50) : new Color(255, 143, 0));

        addToCart.addActionListener(e -> {
            int qty = readQuantity(qtyField);
            cart.put(product, qty);
            updateCartBadge();
            JOptionPane.showMessageDialog(mainPanel, product.name + " added to cart.", "Cart", JOptionPane.INFORMATION_MESSAGE);
        });
        buyNow.addActionListener(e -> {
            int qty = readQuantity(qtyField);
            Map<Product, Integer> singleItem = new LinkedHashMap<>();
            singleItem.put(product, qty);
            openPaymentPanel(singleItem, product.stock <= 0);
        });

        JPanel controls = new JPanel(new BorderLayout(6, 0));
        controls.setOpaque(false);
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.add(minus);
        qtyPanel.add(qtyField);
        qtyPanel.add(plus);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(addToCart);
        actions.add(buyNow);
        controls.add(qtyPanel, BorderLayout.WEST);
        controls.add(actions, BorderLayout.EAST);
        card.add(controls, BorderLayout.SOUTH);
        return card;
    }

    private void refreshProductCards() {
        cardsPanel.removeAll();
        String category = (String) categoryBox.getSelectedItem();
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        for (Product product : products) {
            boolean matchCategory = "All".equals(category) || product.category.equalsIgnoreCase(category);
            boolean matchSearch = keyword.isEmpty() || product.name.toLowerCase().contains(keyword);
            if (matchCategory && matchSearch) {
                cardsPanel.add(createProductCard(product));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void refreshCartPanel() {
        cartItemsPanel.removeAll();
        double grandTotal = 0;
        Map<Product, Integer> items = getNonZeroCartItems();

        if (items.isEmpty()) {
            JLabel empty = label("Your cart is empty.", Font.PLAIN, 18, new Color(18, 56, 124));
            empty.setBorder(new EmptyBorder(16, 12, 0, 0));
            cartItemsPanel.add(empty);
        } else {
            for (Map.Entry<Product, Integer> entry : items.entrySet()) {
                Product product = entry.getKey();
                int qty = entry.getValue();
                grandTotal += product.price * qty;
                cartItemsPanel.add(createCartRow(product, qty));
                cartItemsPanel.add(Box.createVerticalStrut(10));
            }
            JLabel totalLabel = label("Total: " + currencyFormat.format(grandTotal), Font.BOLD, 20, new Color(18, 56, 124));
            totalLabel.setBorder(new EmptyBorder(10, 10, 0, 0));
            cartItemsPanel.add(totalLabel);
        }

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createCartRow(Product product, int qty) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(245, 246, 248));
        row.setBorder(new EmptyBorder(10, 10, 10, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel itemInfo = new JLabel(product.name + " | Price: " + currencyFormat.format(product.price));
        itemInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel qtyLabel = new JLabel(String.valueOf(qty), SwingConstants.CENTER);
        qtyLabel.setPreferredSize(new Dimension(36, 26));
        JButton minus = new JButton("-");
        JButton plus = new JButton("+");
        styleQuantityButton(minus);
        styleQuantityButton(plus);

        minus.addActionListener(e -> {
            int newQty = Math.max(0, cart.get(product) - 1);
            cart.put(product, newQty);
            updateCartBadge();
            refreshCartPanel();
        });
        plus.addActionListener(e -> {
            cart.put(product, cart.get(product) + 1);
            updateCartBadge();
            refreshCartPanel();
        });

        JLabel lineTotal = new JLabel("Total: " + currencyFormat.format(product.price * qty));
        lineTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.add(minus);
        qtyPanel.add(qtyLabel);
        qtyPanel.add(plus);

        row.add(itemInfo, BorderLayout.WEST);
        row.add(qtyPanel, BorderLayout.CENTER);
        row.add(lineTotal, BorderLayout.EAST);
        return row;
    }

    private void openPaymentPanel(Map<Product, Integer> items, boolean preOrder) {
        paymentItems = items;
        paymentIsPreOrder = preOrder;

        double total = 0;
        int totalQty = 0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().price * entry.getValue();
            totalQty += entry.getValue();
        }

        LocalDate estimatedDate = preOrder ? LocalDate.now().plusDays(7) : LocalDate.now().plusDays(2);
        paymentSummaryLabel.setText("Items: " + totalQty + " | Amount: " + currencyFormat.format(total));
        paymentDeliveryLabel.setText("Estimated Delivery: " + estimatedDate);
        paymentNameField.setText(currentUser.fullName);
        paymentAddressField.setText(currentUser.address);
        paymentPhoneField.setText(currentUser.phoneNumber);
        showView(PAYMENT_VIEW);
    }

    private void confirmCheckout() {
        String name = paymentNameField.getText().trim();
        String address = paymentAddressField.getText().trim();
        String phone = paymentPhoneField.getText().trim();
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Name, address, and phone number are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.fullName = name;
        currentUser.address = address;
        currentUser.phoneNumber = phone;
        userLabel.setText(name);

        String mode = paymentIsPreOrder ? "Pre-order placed" : "Order placed";
        JOptionPane.showMessageDialog(mainPanel, mode + " with " + paymentOptionBox.getSelectedItem() + ".", "Success", JOptionPane.INFORMATION_MESSAGE);

        for (Product product : paymentItems.keySet()) {
            cart.put(product, 0);
        }
        paymentItems.clear();
        updateCartBadge();
        refreshCartPanel();
        showView(SHOP_VIEW);
    }

    private Map<Product, Integer> getNonZeroCartItems() {
        Map<Product, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            if (entry.getValue() > 0) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private void showView(String view) {
        contentLayout.show(contentPanel, view);
    }

    private void updateCartBadge() {
        int total = 0;
        for (int qty : cart.values()) {
            total += qty;
        }
        cartBadgeLabel.setText("Cart (" + total + ")");
    }

    private void openProfileDialog() {
        JTextField fullNameField = new JTextField(currentUser.fullName);
        JTextField addressField = new JTextField(currentUser.address);
        JTextField phoneField = new JTextField(currentUser.phoneNumber);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        formPanel.add(new JLabel("Full Name"));
        formPanel.add(fullNameField);
        formPanel.add(new JLabel("Address"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Phone Number"));
        formPanel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(mainPanel, formPanel, "Edit Profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String fullName = fullNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        if (fullName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "All profile fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DataStorage.getInstance().updateCustomerProfile(currentUser.username, fullName, address, phone);
            currentUser.fullName = fullName;
            currentUser.address = address;
            currentUser.phoneNumber = phone;
            userLabel.setText(fullName);
            JOptionPane.showMessageDialog(mainPanel, "Profile updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(mainPanel, exception.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seedProducts() {
        products.add(new Product("Wireless Mouse", "Accessories", 14.99, 43, "https://images.unsplash.com/photo-1527814050087-3793815479db?w=640"));
        products.add(new Product("Mechanical Keyboard", "Accessories", 49.99, 18, "https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=640"));
        products.add(new Product("Laptop Stand", "Accessories", 27.50, 21, "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=640"));
        products.add(new Product("USB-C Hub", "Accessories", 22.75, 30, "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=640"));
        products.add(new Product("Webcam 1080p", "Accessories", 34.00, 17, "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=640"));

        products.add(new Product("Notebook Set", "Office", 8.40, 85, "https://images.unsplash.com/photo-1455390582262-044cdead277a?w=640"));
        products.add(new Product("Desk Lamp", "Office", 19.20, 26, "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=640"));
        products.add(new Product("Stapler", "Office", 6.25, 40, "https://images.unsplash.com/photo-1586075010923-2dd4570fb338?w=640"));
        products.add(new Product("Paper Ream A4", "Office", 5.10, 95, "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=640"));

        products.add(new Product("HDMI Cable 2m", "Cables", 6.90, 0, "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=640"));
        products.add(new Product("USB-C Cable", "Cables", 5.60, 120, "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=640"));
        products.add(new Product("Ethernet Cable Cat6", "Cables", 4.75, 68, "https://images.unsplash.com/photo-1597733336794-12d05021d510?w=640"));

        products.add(new Product("External SSD 1TB", "Storage", 79.00, 9, "https://images.unsplash.com/photo-1591488320449-011701bb6704?w=640"));
        products.add(new Product("Flash Drive 64GB", "Storage", 12.50, 0, "https://images.unsplash.com/photo-1587037542794-6db8884df0a9?w=640"));
        products.add(new Product("MicroSD 128GB", "Storage", 15.90, 22, "https://images.unsplash.com/photo-1600348712270-9b1460cc9a6c?w=640"));

        products.add(new Product("Bluetooth Headset", "Audio", 24.90, 14, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=640"));
        products.add(new Product("USB Speaker", "Audio", 18.75, 19, "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=640"));
        products.add(new Product("Condenser Microphone", "Audio", 39.99, 0, "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=640"));

        products.add(new Product("Wi-Fi Router", "Networking", 45.00, 11, "https://images.unsplash.com/photo-1647427060118-4911c9821b82?w=640"));
        products.add(new Product("8-Port Switch", "Networking", 28.30, 16, "https://images.unsplash.com/photo-1629654297299-c8506221ca97?w=640"));
        products.add(new Product("Range Extender", "Networking", 31.40, 0, "https://images.unsplash.com/photo-1518770660439-4636190af475?w=640"));

        for (Product product : products) {
            cart.put(product, 0);
        }
    }

    private JLabel label(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        return label;
    }

    private Icon loadProductImage(String url) {
        try {
            ImageIcon icon = new ImageIcon(new URL(url));
            Image scaled = icon.getImage().getScaledInstance(210, 130, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ignored) {
            BufferedImage fallback = new BufferedImage(210, 130, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setPaint(new GradientPaint(0, 0, new Color(52, 152, 219), 210, 130, new Color(41, 128, 185)));
            g2.fillRect(0, 0, 210, 130);
            g2.dispose();
            return new ImageIcon(fallback);
        }
    }

    private void styleHeaderButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
    }

    private void styleQuantityButton(JButton button) {
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(40, 28));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    private void styleActionButton(JButton button, Color bg) {
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(6, 10, 6, 10));
    }

    private int readQuantity(JTextField qtyField) {
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) {
                qtyField.setText("1");
                return 1;
            }
            return qty;
        } catch (NumberFormatException exception) {
            qtyField.setText("1");
            return 1;
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private static class Product {
        private final String name;
        private final String category;
        private final double price;
        private final int stock;
        private final String imageUrl;

        private Product(String name, String category, double price, int stock, String imageUrl) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.imageUrl = imageUrl;
        }
    }
}
