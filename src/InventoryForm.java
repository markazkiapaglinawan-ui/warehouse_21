import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryForm {
    private static final String[] ITEM_CATEGORIES = {
            "Office Supplies",
            "Cleaning Supplies",
            "Food and Beverages",
            "Packaging Materials",
            "Tools and Equipment",
            "Electronics",
            "Furniture",
            "Raw Materials",
            "Toys"
    };

    private static final String OVERVIEW_CARD = "overview";
    private static final String ADD_CARD = "add";
    private static final String EDIT_CARD = "edit";
    private static final String SEARCH_CARD = "search";
    private static final String DELETE_CARD = "delete";

    private final UserRole userRole;
    private final CardLayout contentLayout = new CardLayout();

    private JPanel mainPanel;
    private JPanel contentPanel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel statusLabel;

    private JTextField addIdField;
    private JTextField addNameField;
    private JComboBox<String> addCategoryBox;
    private JTextField addQuantityField;
    private JTextField addPriceField;
    private JTextField addAisleField;
    private JTextField addRackField;
    private JTextField addBinField;

    private JComboBox<String> editLookupBox;
    private JTextField editNameField;
    private JComboBox<String> editCategoryBox;
    private JTextField editQuantityField;
    private JTextField editPriceField;
    private JTextField editAisleField;
    private JTextField editRackField;
    private JTextField editBinField;
    private JLabel editSelectedItemLabel;

    private JTextField searchField;
    private JTextArea searchResultsArea;

    private JTextField deleteItemField;
    private JTextArea deletePreviewArea;

    private DataStorage.Item loadedEditItem;

    public InventoryForm() {
        this(UserRole.ADMIN);
    }

    public InventoryForm(UserRole userRole) {
        this.userRole = userRole;
        if (mainPanel == null) {
            buildUi();
        } else {
            configureBoundUi();
        }
        applyRoleAccess();
        showOverview();
    }

    private void configureBoundUi() {
        mainPanel.setBackground(ModuleTheme.PAGE_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel.setText("Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(ModuleTheme.TITLE_COLOR);

        descriptionLabel.setText("Select an inventory option from the top menu.");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionLabel.setForeground(ModuleTheme.SUBTITLE_COLOR);

        contentPanel.removeAll();
        contentPanel.setLayout(contentLayout);
        contentPanel.setOpaque(false);
        contentPanel.add(createOverviewPanel(), OVERVIEW_CARD);
        contentPanel.add(createAddPanel(), ADD_CARD);
        contentPanel.add(createEditPanel(), EDIT_CARD);
        contentPanel.add(createSearchPanel(), SEARCH_CARD);
        contentPanel.add(createDeletePanel(), DELETE_CARD);

        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(46, 125, 50));
    }

    private void buildUi() {
        mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(ModuleTheme.PAGE_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(ModuleTheme.TITLE_COLOR);

        descriptionLabel = new JLabel("Select an inventory option from the top menu.");
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
        contentPanel.add(createAddPanel(), ADD_CARD);
        contentPanel.add(createEditPanel(), EDIT_CARD);
        contentPanel.add(createSearchPanel(), SEARCH_CARD);
        contentPanel.add(createDeletePanel(), DELETE_CARD);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(46, 125, 50));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.add(statusLabel, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(16, 0));

        JLabel message = new JLabel("<html><div style='width:520px;'>Use the Inventory dropdown in the top navigation bar to open Add Item, Edit Item, Search Item, or Delete Item in this same panel.</div></html>");
        message.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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

                // Base frame
                g2.setColor(new Color(22, 57, 104, 60));
                g2.fillRoundRect(pad, pad, w - pad * 2, h - pad * 2, 12, 12);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.drawRoundRect(pad, pad, w - pad * 2, h - pad * 2, 12, 12);

                int centerX = w / 2;
                int floorY = h - 58;

                // Floor
                g2.setColor(new Color(255, 255, 255, 65));
                g2.fillRoundRect(pad + 22, floorY, w - (pad + 22) * 2, 8, 8, 8);

                // Shelves
                int shelfW = 130;
                int shelfH = 180;
                int shelfTop = floorY - shelfH;
                int leftX = centerX - shelfW - 20;
                int rightX = centerX + 20;
                drawShelf(g2, leftX, shelfTop, shelfW, shelfH);
                drawShelf(g2, rightX, shelfTop, shelfW, shelfH);

                // Center box stack
                drawBox(g2, centerX - 34, floorY - 72, 68, 36, new Color(253, 214, 116, 180));
                drawBox(g2, centerX - 28, floorY - 106, 56, 30, new Color(250, 196, 93, 180));
            }

            private void drawShelf(Graphics2D g2, int x, int y, int w, int h) {
                g2.setColor(new Color(255, 255, 255, 95));
                g2.drawRoundRect(x, y, w, h, 10, 10);
                g2.drawLine(x + 10, y + 60, x + w - 10, y + 60);
                g2.drawLine(x + 10, y + 115, x + w - 10, y + 115);

                drawBox(g2, x + 14, y + 18, 34, 24, new Color(123, 179, 255, 180));
                drawBox(g2, x + 54, y + 16, 30, 26, new Color(107, 160, 244, 180));
                drawBox(g2, x + 88, y + 18, 26, 24, new Color(88, 143, 231, 180));

                drawBox(g2, x + 16, y + 72, 44, 26, new Color(122, 217, 194, 180));
                drawBox(g2, x + 66, y + 70, 44, 28, new Color(95, 200, 175, 180));

                drawBox(g2, x + 20, y + 128, 36, 30, new Color(251, 200, 111, 180));
                drawBox(g2, x + 62, y + 130, 26, 28, new Color(246, 183, 92, 180));
                drawBox(g2, x + 92, y + 132, 22, 26, new Color(235, 167, 79, 180));
            }

            private void drawBox(Graphics2D g2, int x, int y, int w, int h, Color color) {
                g2.setColor(color);
                g2.fillRoundRect(x, y, w, h, 6, 6);
                g2.setColor(new Color(255, 255, 255, 110));
                g2.drawRoundRect(x, y, w, h, 6, 6);
            }
        };
        illustration.setOpaque(false);
        illustration.setPreferredSize(new Dimension(520, 280));
        return illustration;
    }

    private JPanel createAddPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        addIdField = new JTextField();
        addNameField = new JTextField();
        addCategoryBox = new JComboBox<>(ITEM_CATEGORIES);
        addQuantityField = new JTextField();
        addPriceField = new JTextField();
        addAisleField = new JTextField();
        addRackField = new JTextField();
        addBinField = new JTextField();

        JButton saveButton = createActionButton(userRole == UserRole.STAFF ? "Save Stock-In Item" : "Save Item");
        saveButton.addActionListener(event -> handleAddItem());

        panel.add(createSectionTitle(userRole == UserRole.STAFF ? "Stock-In Item" : "Add Item"), BorderLayout.NORTH);
        panel.add(createFormScrollPane(createFormStack(
                "Item ID", addIdField,
                "Item Name", addNameField,
                "Category", addCategoryBox,
                "Initial Quantity", addQuantityField,
                "Price", addPriceField,
                "Aisle", addAisleField,
                "Rack", addRackField,
                "Bin", addBinField
        )), BorderLayout.CENTER);
        panel.add(createButtonRow(saveButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        editLookupBox = new JComboBox<>();
        JButton loadButton = createSecondaryButton("Load Item");
        loadButton.addActionListener(event -> loadItemForEdit());
        editLookupBox.addActionListener(event -> loadItemForEdit());
        topPanel.add(labeledComponent("Item ID", editLookupBox), BorderLayout.CENTER);
        topPanel.add(loadButton, BorderLayout.EAST);

        editSelectedItemLabel = new JLabel("No item loaded.");
        editNameField = new JTextField();
        editCategoryBox = new JComboBox<>(ITEM_CATEGORIES);
        editQuantityField = new JTextField();
        editPriceField = new JTextField();
        editAisleField = new JTextField();
        editRackField = new JTextField();
        editBinField = new JTextField();

        JButton updateButton = createActionButton("Update Item");
        updateButton.addActionListener(event -> handleEditItem());

        JPanel headerPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel.setOpaque(false);
        headerPanel.add(createSectionTitle("Edit Item"), BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(createFormScrollPane(createFormStack(
                "Selected Item", editSelectedItemLabel,
                "Item Name", editNameField,
                "Category", editCategoryBox,
                "Quantity", editQuantityField,
                "Price", editPriceField,
                "Aisle", editAisleField,
                "Rack", editRackField,
                "Bin", editBinField
        )), BorderLayout.CENTER);
        panel.add(createButtonRow(updateButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        searchField = new JTextField();
        JButton searchButton = createActionButton("Search");
        searchButton.addActionListener(event -> handleSearchItems());
        topPanel.add(labeledField("Search by ID, name, or category", searchField), BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        searchResultsArea = createTextArea();
        panel.add(createSectionTitle("Search Item"), BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(new JScrollPane(searchResultsArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDeletePanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        deleteItemField = new JTextField();
        JButton previewButton = createSecondaryButton("Preview Item");
        previewButton.addActionListener(event -> handlePreviewDelete());
        topPanel.add(labeledField("Item ID", deleteItemField), BorderLayout.CENTER);
        topPanel.add(previewButton, BorderLayout.EAST);

        deletePreviewArea = createTextArea();
        JButton deleteButton = createDangerButton("Delete Item");
        deleteButton.addActionListener(event -> handleDeleteItem());

        panel.add(createSectionTitle("Delete Item"), BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(new JScrollPane(deletePreviewArea), BorderLayout.CENTER);
        panel.add(createButtonRow(deleteButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSurfacePanel() {
        return ModuleTheme.createSurfacePanel();
    }

    private JLabel createSectionTitle(String text) {
        return ModuleTheme.createSectionTitle(text);
    }

    private JPanel labeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JLabel label = ModuleTheme.createFieldLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        ModuleTheme.styleInputField(field);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel labeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JLabel label = ModuleTheme.createFieldLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        if (component instanceof JTextField textField) {
            ModuleTheme.styleInputField(textField);
        } else if (component instanceof JComboBox<?> comboBox) {
            ModuleTheme.styleComboBox(comboBox);
        } else {
            component.setForeground(ModuleTheme.SURFACE_TEXT);
        }
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormStack(Object... fields) {
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        for (int i = 0; i < fields.length; i += 2) {
            String label = (String) fields[i];
            JComponent component = (JComponent) fields[i + 1];
            stack.add(labeledComponent(label, component));
            if (i + 2 < fields.length) {
                stack.add(Box.createVerticalStrut(12));
            }
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(stack, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel createButtonRow(JButton button) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.add(button);
        return panel;
    }

    private JScrollPane createFormScrollPane(JComponent formContent) {
        JScrollPane scrollPane = new JScrollPane(formContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        ModuleTheme.styleTextArea(area);
        return area;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        ModulePanelFactory.styleActionButton(button, new Color(32, 136, 203));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        ModulePanelFactory.styleActionButton(button, new Color(92, 107, 128));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        ModulePanelFactory.styleActionButton(button, new Color(211, 47, 47));
        return button;
    }

    private void applyRoleAccess() {
        if (userRole == UserRole.STAFF) {
            descriptionLabel.setText("Select a stock action from the top menu. Staff can add stock and search records.");
        }
    }

    private void showOverview() {
        descriptionLabel.setText("Select an inventory option from the top menu.");
        statusLabel.setText(" ");
        contentLayout.show(contentPanel, OVERVIEW_CARD);
    }

    private void showAddPanel() {
        descriptionLabel.setText(userRole == UserRole.STAFF
                ? "Record an incoming stock item in this panel."
                : "Add a new inventory item in this panel.");
        statusLabel.setText(" ");
        addIdField.setText("ITM-" + (DataStorage.getInstance().getItems().size() + 1));
        addNameField.setText("");
        addCategoryBox.setSelectedIndex(0);
        addQuantityField.setText("");
        addPriceField.setText("");
        addAisleField.setText("");
        addRackField.setText("");
        addBinField.setText("");
        contentLayout.show(contentPanel, ADD_CARD);
    }

    private void showEditPanel() {
        descriptionLabel.setText("Load an item, update its details, and save it here.");
        statusLabel.setText(" ");
        refreshEditItemSelection();
        editSelectedItemLabel.setText("No item loaded.");
        editNameField.setText("");
        editCategoryBox.setSelectedIndex(0);
        editQuantityField.setText("");
        editPriceField.setText("");
        editAisleField.setText("");
        editRackField.setText("");
        editBinField.setText("");
        loadedEditItem = null;
        contentLayout.show(contentPanel, EDIT_CARD);
    }

    private void showSearchPanel() {
        descriptionLabel.setText("Search inventory records in this panel.");
        statusLabel.setText(" ");
        searchField.setText("");
        searchResultsArea.setText("");
        contentLayout.show(contentPanel, SEARCH_CARD);
    }

    private void showDeletePanel() {
        descriptionLabel.setText("Preview and delete an item in this panel.");
        statusLabel.setText(" ");
        deleteItemField.setText("");
        deletePreviewArea.setText("");
        contentLayout.show(contentPanel, DELETE_CARD);
    }

    private void handleAddItem() {
        try {
            String id = addIdField.getText().trim();
            String name = addNameField.getText().trim();
            String category = (String) addCategoryBox.getSelectedItem();
            int quantity = Integer.parseInt(addQuantityField.getText().trim());
            double price = Double.parseDouble(addPriceField.getText().trim());
            String aisle = addAisleField.getText().trim();
            String rack = addRackField.getText().trim();
            String bin = addBinField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                throw new IllegalArgumentException("Item ID and item name are required.");
            }
            if (aisle.isEmpty() || rack.isEmpty() || bin.isEmpty()) {
                throw new IllegalArgumentException("Aisle, rack, and bin are required.");
            }

            DataStorage.Item newItem = new DataStorage.Item(id, name, category, quantity, price, aisle, rack, bin);
            DataStorage.getInstance().addItem(newItem);
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Item " + id + " added successfully.",
                    "Item Added",
                    JOptionPane.INFORMATION_MESSAGE
            );
            statusLabel.setForeground(new Color(46, 125, 50));
            statusLabel.setText("Item " + id + " saved successfully.");
            showAddPanel();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void loadItemForEdit() {
        Object selected = editLookupBox.getSelectedItem();
        String itemId = selected == null ? "" : selected.toString().trim();
        if (itemId.isEmpty()) {
            loadedEditItem = null;
            editSelectedItemLabel.setText("No item loaded.");
            editNameField.setText("");
            editCategoryBox.setSelectedIndex(0);
            editQuantityField.setText("");
            editPriceField.setText("");
            editAisleField.setText("");
            editRackField.setText("");
            editBinField.setText("");
            return;
        }

        loadedEditItem = DataStorage.getInstance().findItemById(itemId);
        if (loadedEditItem == null) {
            showError("Item ID '" + itemId + "' not found.");
            return;
        }

        editSelectedItemLabel.setText(loadedEditItem.id);
        editNameField.setText(loadedEditItem.name);
        setSelectedCategory(editCategoryBox, loadedEditItem.category);
        editQuantityField.setText(String.valueOf(loadedEditItem.quantity));
        editPriceField.setText(String.valueOf(loadedEditItem.price));
        editAisleField.setText(loadedEditItem.aisle);
        editRackField.setText(loadedEditItem.rack);
        editBinField.setText(loadedEditItem.binCode);
        statusLabel.setForeground(new Color(46, 125, 50));
        statusLabel.setText("Loaded item " + loadedEditItem.id + ".");
    }

    private void refreshEditItemSelection() {
        List<DataStorage.Item> items = DataStorage.getInstance().getItems();
        editLookupBox.removeAllItems();
        for (DataStorage.Item item : items) {
            editLookupBox.addItem(item.id);
        }
        if (editLookupBox.getItemCount() > 0) {
            editLookupBox.setSelectedIndex(0);
        }
    }

    private void handleEditItem() {
        if (loadedEditItem == null) {
            showError("Load an item before updating it.");
            return;
        }

        try {
            String name = editNameField.getText().trim();
            String category = (String) editCategoryBox.getSelectedItem();
            int quantity = Integer.parseInt(editQuantityField.getText().trim());
            double price = Double.parseDouble(editPriceField.getText().trim());
            String aisle = editAisleField.getText().trim();
            String rack = editRackField.getText().trim();
            String bin = editBinField.getText().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Item name is required.");
            }
            if (aisle.isEmpty() || rack.isEmpty() || bin.isEmpty()) {
                throw new IllegalArgumentException("Aisle, rack, and bin are required.");
            }

            DataStorage.Item updatedItem = new DataStorage.Item(loadedEditItem.id, name, category, quantity, price, aisle, rack, bin);
            DataStorage.getInstance().updateItem(loadedEditItem.id, updatedItem);
            loadedEditItem = updatedItem;
            statusLabel.setForeground(new Color(46, 125, 50));
            statusLabel.setText("Item " + updatedItem.id + " updated successfully.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleSearchItems() {
        String query = searchField.getText().trim().toLowerCase();
        List<DataStorage.Item> items = DataStorage.getInstance().getItems();
        List<DataStorage.Item> matches = new ArrayList<>();

        for (DataStorage.Item item : items) {
            String category = item.category == null ? "" : item.category.toLowerCase();
            String aisle = item.aisle == null ? "" : item.aisle.toLowerCase();
            String rack = item.rack == null ? "" : item.rack.toLowerCase();
            String bin = item.binCode == null ? "" : item.binCode.toLowerCase();
            if (query.isEmpty()
                    || item.id.toLowerCase().contains(query)
                    || item.name.toLowerCase().contains(query)
                    || category.contains(query)
                    || aisle.contains(query)
                    || rack.contains(query)
                    || bin.contains(query)) {
                matches.add(item);
            }
        }

        if (matches.isEmpty()) {
            searchResultsArea.setText("No items found.");
            statusLabel.setForeground(new Color(211, 47, 47));
            statusLabel.setText("No items matched the search.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (DataStorage.Item item : matches) {
            builder.append("Item ID: ").append(item.id).append('\n');
            builder.append("Name: ").append(item.name).append('\n');
            builder.append("Category: ").append(item.category).append('\n');
            builder.append("Quantity: ").append(item.quantity).append('\n');
            builder.append("Price: ").append(formatCurrency(item.price)).append("\n\n");
            builder.append("Location: Aisle ").append(item.aisle).append(" / Rack ").append(item.rack).append(" / Bin ").append(item.binCode).append("\n\n");
        }
        searchResultsArea.setText(builder.toString().trim());
        statusLabel.setForeground(new Color(46, 125, 50));
        statusLabel.setText(matches.size() + " item(s) found.");
    }

    private void handlePreviewDelete() {
        String itemId = deleteItemField.getText().trim();
        if (itemId.isEmpty()) {
            showError("Enter an item ID first.");
            return;
        }

        DataStorage.Item item = DataStorage.getInstance().findItemById(itemId);
        if (item == null) {
            deletePreviewArea.setText("");
            showError("Item ID '" + itemId + "' not found.");
            return;
        }

        deletePreviewArea.setText(
                "Item ID: " + item.id + "\n" +
                "Name: " + item.name + "\n" +
                "Category: " + item.category + "\n" +
                "Quantity: " + item.quantity + "\n" +
                "Price: " + formatCurrency(item.price) + "\n" +
                "Location: Aisle " + item.aisle + " / Rack " + item.rack + " / Bin " + item.binCode
        );
        statusLabel.setForeground(new Color(46, 125, 50));
        statusLabel.setText("Item " + item.id + " is ready to delete.");
    }

    private void handleDeleteItem() {
        String itemId = deleteItemField.getText().trim();
        if (itemId.isEmpty()) {
            showError("Enter an item ID first.");
            return;
        }

        try {
            DataStorage.getInstance().deleteItem(itemId);
            deletePreviewArea.setText("");
            deleteItemField.setText("");
            statusLabel.setForeground(new Color(46, 125, 50));
            statusLabel.setText("Item " + itemId + " deleted successfully.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setForeground(new Color(211, 47, 47));
        statusLabel.setText(message);
    }

    private void setSelectedCategory(JComboBox<String> comboBox, String category) {
        if (category == null || category.isBlank()) {
            comboBox.setSelectedIndex(0);
            return;
        }

        ComboBoxModel<String> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (category.equalsIgnoreCase(model.getElementAt(i))) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }

        comboBox.addItem(category);
        comboBox.setSelectedItem(category);
    }

    private String formatCurrency(double amount) {
        return String.format("PHP %.2f", amount);
    }

    public void showAction(String actionKey) {
        switch (actionKey) {
            case "add" -> showAddPanel();
            case "edit" -> showEditPanel();
            case "search" -> showSearchPanel();
            case "delete" -> showDeletePanel();
            default -> throw new IllegalArgumentException("Unknown inventory action: " + actionKey);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
