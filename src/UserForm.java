import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserForm {
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

    private JTextField addUsernameField;
    private JPasswordField addPasswordField;
    private JComboBox<UserRole> addRoleBox;

    private JTextField editLookupField;
    private JLabel editSelectedUserLabel;
    private JPasswordField editPasswordField;
    private JComboBox<UserRole> editRoleBox;

    private JTextField searchField;
    private JTextArea searchResultsArea;

    private JTextField deleteUserField;
    private JTextArea deletePreviewArea;

    private DataStorage.User loadedUser;

    public UserForm() {
        this(UserRole.ADMIN);
    }

    public UserForm(UserRole userRole) {
        this.userRole = userRole;
        buildUi();
        applyRoleAccess();
        showOverview();
    }

    private void buildUi() {
        mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(ModuleTheme.PAGE_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(ModuleTheme.TITLE_COLOR);

        descriptionLabel = new JLabel("Select a user option from the top menu.");
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
        JLabel message = new JLabel("<html><div style='width:520px;'>Use the Users dropdown in the top navigation bar to open Add User, Edit User, Search User, or Delete User in this same panel.</div></html>");
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

                int left = pad + 30;
                int top = pad + 28;
                int avatarSize = 52;
                int rowGap = 18;
                int rowH = 66;
                int rowW = frameW - 60;

                for (int i = 0; i < 3; i++) {
                    int y = top + i * (rowH + rowGap);
                    g2.setColor(new Color(255, 255, 255, 38));
                    g2.fillRoundRect(left, y, rowW, rowH, 10, 10);
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.drawRoundRect(left, y, rowW, rowH, 10, 10);

                    g2.setColor(new Color(122, 217, 194, 180));
                    g2.fillOval(left + 10, y + 7, avatarSize, avatarSize);
                    g2.setColor(new Color(255, 255, 255, 120));
                    g2.drawOval(left + 10, y + 7, avatarSize, avatarSize);

                    g2.setColor(new Color(123, 179, 255, 180));
                    g2.fillRoundRect(left + 76, y + 16, rowW - 176, 12, 6, 6);
                    g2.fillRoundRect(left + 76, y + 36, rowW - 250, 10, 5, 5);

                    g2.setColor(new Color(251, 200, 111, 180));
                    g2.fillRoundRect(left + rowW - 86, y + 20, 64, 24, 8, 8);
                }
                g2.dispose();
            }
        };
        illustration.setOpaque(false);
        illustration.setPreferredSize(new Dimension(520, 280));
        return illustration;
    }

    private JPanel createAddPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        addUsernameField = new JTextField();
        addPasswordField = new JPasswordField();
        addRoleBox = new JComboBox<>(UserRole.values());

        JButton addButton = createActionButton("Save User");
        addButton.addActionListener(event -> handleAddUser());

        panel.add(createSectionTitle("Add User"), BorderLayout.NORTH);
        panel.add(createFormStack(
                "Username", addUsernameField,
                "Password", addPasswordField,
                "Role", addRoleBox
        ), BorderLayout.CENTER);
        panel.add(createButtonRow(addButton), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        editLookupField = new JTextField();
        JButton loadButton = createSecondaryButton("Load User");
        loadButton.addActionListener(event -> loadUserForEdit());
        topPanel.add(labeledField("Username", editLookupField), BorderLayout.CENTER);
        topPanel.add(loadButton, BorderLayout.EAST);

        editSelectedUserLabel = new JLabel("No user loaded.");
        editPasswordField = new JPasswordField();
        editRoleBox = new JComboBox<>(UserRole.values());

        JButton updateButton = createActionButton("Update User");
        updateButton.addActionListener(event -> handleEditUser());

        panel.add(createSectionTitle("Edit User"), BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(createFormStack(
                "Selected User", editSelectedUserLabel,
                "New Password", editPasswordField,
                "Role", editRoleBox
        ), BorderLayout.CENTER);
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
        searchButton.addActionListener(event -> handleSearchUsers());
        topPanel.add(labeledField("Search by username or role", searchField), BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        searchResultsArea = createTextArea();
        panel.add(createSectionTitle("Search User"), BorderLayout.NORTH);
        panel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(new JScrollPane(searchResultsArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDeletePanel() {
        JPanel panel = createSurfacePanel();
        panel.setLayout(new BorderLayout(0, 16));

        JPanel topPanel = new JPanel(new BorderLayout(12, 0));
        topPanel.setOpaque(false);
        deleteUserField = new JTextField();
        JButton previewButton = createSecondaryButton("Preview User");
        previewButton.addActionListener(event -> handlePreviewDelete());
        topPanel.add(labeledField("Username", deleteUserField), BorderLayout.CENTER);
        topPanel.add(previewButton, BorderLayout.EAST);

        deletePreviewArea = createTextArea();
        JButton deleteButton = createDangerButton("Delete User");
        deleteButton.addActionListener(event -> handleDeleteUser());

        panel.add(createSectionTitle("Delete User"), BorderLayout.NORTH);
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

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        ModuleTheme.styleTextArea(area);
        return area;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        ModulePanelFactory.styleActionButton(button, new Color(0, 150, 136));
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
            descriptionLabel.setText("Only admins can manage users.");
        }
    }

    private void showOverview() {
        descriptionLabel.setText(userRole == UserRole.ADMIN
                ? "Select a user option from the top menu."
                : "Only admins can manage users.");
        clearStatus();
        contentLayout.show(contentPanel, OVERVIEW_CARD);
    }

    private void showAddPanel() {
        if (!ensureAdminAccess()) {
            return;
        }
        descriptionLabel.setText("Add a user in this panel.");
        clearStatus();
        addUsernameField.setText("");
        addPasswordField.setText("");
        addRoleBox.setSelectedItem(UserRole.STAFF);
        contentLayout.show(contentPanel, ADD_CARD);
    }

    private void showEditPanel() {
        if (!ensureAdminAccess()) {
            return;
        }
        descriptionLabel.setText("Load and edit a user in this panel.");
        clearStatus();
        editLookupField.setText("");
        editSelectedUserLabel.setText("No user loaded.");
        editPasswordField.setText("");
        editRoleBox.setSelectedItem(UserRole.STAFF);
        loadedUser = null;
        contentLayout.show(contentPanel, EDIT_CARD);
    }

    private void showSearchPanel() {
        if (!ensureAdminAccess()) {
            return;
        }
        descriptionLabel.setText("Search user records in this panel.");
        clearStatus();
        searchField.setText("");
        searchResultsArea.setText("");
        contentLayout.show(contentPanel, SEARCH_CARD);
    }

    private void showDeletePanel() {
        if (!ensureAdminAccess()) {
            return;
        }
        descriptionLabel.setText("Preview and delete a user in this panel.");
        clearStatus();
        deleteUserField.setText("");
        deletePreviewArea.setText("");
        contentLayout.show(contentPanel, DELETE_CARD);
    }

    private boolean ensureAdminAccess() {
        if (userRole == UserRole.ADMIN) {
            return true;
        }
        showError("Only admins can manage users.");
        contentLayout.show(contentPanel, OVERVIEW_CARD);
        return false;
    }

    private void handleAddUser() {
        try {
            String username = addUsernameField.getText().trim();
            String password = new String(addPasswordField.getPassword()).trim();
            UserRole role = (UserRole) addRoleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Username and password are required.");
            }

            DataStorage.getInstance().addUser(new DataStorage.User(username, password, role));
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "User created successfully.",
                    "User Created",
                    JOptionPane.INFORMATION_MESSAGE
            );
            showSuccess("User " + username + " created successfully.");
            showAddPanel();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void loadUserForEdit() {
        String username = editLookupField.getText().trim();
        if (username.isEmpty()) {
            showError("Enter a username first.");
            return;
        }

        loadedUser = DataStorage.getInstance().findUserByUsername(username);
        if (loadedUser == null) {
            showError("User '" + username + "' not found.");
            return;
        }

        editSelectedUserLabel.setText(loadedUser.username);
        editPasswordField.setText("");
        editRoleBox.setSelectedItem(loadedUser.role);
        showSuccess("Loaded user " + loadedUser.username + ".");
    }

    private void handleEditUser() {
        if (loadedUser == null) {
            showError("Load a user before updating.");
            return;
        }

        try {
            String password = new String(editPasswordField.getPassword()).trim();
            UserRole role = (UserRole) editRoleBox.getSelectedItem();
            DataStorage.getInstance().updateUser(loadedUser.username, password, role);
            loadedUser = DataStorage.getInstance().findUserByUsername(loadedUser.username);
            showSuccess("User " + editSelectedUserLabel.getText() + " updated successfully.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleSearchUsers() {
        String query = searchField.getText().trim().toLowerCase();
        List<DataStorage.User> matches = new ArrayList<>();
        for (DataStorage.User user : DataStorage.getInstance().getUsers()) {
            if (query.isEmpty()
                    || user.username.toLowerCase().contains(query)
                    || user.role.name().toLowerCase().contains(query)) {
                matches.add(user);
            }
        }

        if (matches.isEmpty()) {
            searchResultsArea.setText("No users found.");
            showError("No users matched the search.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (DataStorage.User user : matches) {
            builder.append("Username: ").append(user.username).append('\n');
            builder.append("Role: ").append(user.role.getDisplayName()).append('\n');
            builder.append("Status: Active").append("\n\n");
        }
        searchResultsArea.setText(builder.toString().trim());
        showSuccess(matches.size() + " user(s) found.");
    }

    private void handlePreviewDelete() {
        String username = deleteUserField.getText().trim();
        if (username.isEmpty()) {
            showError("Enter a username first.");
            return;
        }

        DataStorage.User user = DataStorage.getInstance().findUserByUsername(username);
        if (user == null) {
            deletePreviewArea.setText("");
            showError("User '" + username + "' not found.");
            return;
        }
        if (user.username.equalsIgnoreCase("admin")) {
            deletePreviewArea.setText("");
            showError("Cannot delete the primary administrator account.");
            return;
        }

        deletePreviewArea.setText(
                "Username: " + user.username + "\n" +
                "Role: " + user.role.getDisplayName() + "\n" +
                "Status: Active"
        );
        showSuccess("User " + user.username + " is ready to delete.");
    }

    private void handleDeleteUser() {
        String username = deleteUserField.getText().trim();
        if (username.isEmpty()) {
            showError("Enter a username first.");
            return;
        }

        try {
            DataStorage.getInstance().deleteUser(username);
            deleteUserField.setText("");
            deletePreviewArea.setText("");
            showSuccess("User " + username + " deleted successfully.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void clearStatus() {
        statusLabel.setText(" ");
    }

    private void showSuccess(String message) {
        statusLabel.setForeground(new Color(46, 125, 50));
        statusLabel.setText(message);
    }

    private void showError(String message) {
        statusLabel.setForeground(new Color(211, 47, 47));
        statusLabel.setText(message);
    }

    public void showAction(String actionKey) {
        switch (actionKey) {
            case "add" -> showAddPanel();
            case "edit" -> showEditPanel();
            case "search" -> showSearchPanel();
            case "delete" -> showDeletePanel();
            default -> throw new IllegalArgumentException("Unknown user action: " + actionKey);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
