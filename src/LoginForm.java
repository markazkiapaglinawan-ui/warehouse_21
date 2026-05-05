import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public class LoginForm {
    private static final String LOGIN_BACKGROUND_PATH = "assets/login-background.png";

    private JPanel mainPanel;
    private JPanel loginCardPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private final Consumer<DataStorage.User> loginSuccessHandler;

    public LoginForm() {
        this(null);
    }

    public LoginForm(Consumer<DataStorage.User> loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
        if (!(mainPanel instanceof BackgroundPanel)) {
            buildFallbackUi();
        }
        styleUi();
        wireActions();
    }

    private void buildFallbackUi() {
        mainPanel = new BackgroundPanel(loadBackgroundImage());
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(48, 48, 48, 48));

        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setOpaque(true);
        loginCard.setBackground(new Color(255, 255, 255, 235));
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 223, 235)),
                new EmptyBorder(30, 30, 30, 30)
        ));
        loginCard.setPreferredSize(new Dimension(480, 340));
        loginCardPanel = loginCard;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        titleLabel = new JLabel("Warehouse Login");
        subtitleLabel = new JLabel("Please enter your username and password to access the system.");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
        exitButton = new JButton("Exit");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginCard.add(titleLabel, gbc);

        gbc.gridy = 1;
        loginCard.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        loginCard.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        loginCard.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginCard.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        loginCard.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        loginCard.add(loginButton, gbc);
        gbc.gridx = 1;
        loginCard.add(signUpButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        loginCard.add(exitButton, gbc);

        mainPanel.add(loginCard);
    }

    private void styleUi() {
        mainPanel.setBackground(new Color(14, 70, 132));
        if (loginCardPanel != null) {
            loginCardPanel.setBackground(new Color(255, 255, 255, 235));
            loginCardPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(214, 223, 235)),
                    new EmptyBorder(30, 30, 30, 30)
            ));
        }
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(25, 42, 86));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(92, 107, 128));

        styleButton(loginButton, new Color(25, 118, 210));
        styleButton(signUpButton, new Color(46, 125, 50));
        styleButton(exitButton, new Color(229, 57, 53));
    }

    private void styleButton(JButton button, Color background) {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(14, 24, 14, 24));
        button.setPreferredSize(new Dimension(140, 50));
    }

    private void wireActions() {
        loginButton.addActionListener(event -> handleLogin());
        signUpButton.addActionListener(event -> openSignUpDialog());
        exitButton.addActionListener(event -> {
            System.exit(0);
        });
    }

    private void openSignUpDialog() {
        JTextField fullNameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField usernameInput = new JTextField();
        JPasswordField passwordInput = new JPasswordField();

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        formPanel.add(new JLabel("Full Name"));
        formPanel.add(fullNameField);
        formPanel.add(new JLabel("Address"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Phone Number"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Username"));
        formPanel.add(usernameInput);
        formPanel.add(new JLabel("Password"));
        formPanel.add(passwordInput);

        int result = JOptionPane.showConfirmDialog(
                mainPanel,
                formPanel,
                "Customer Sign Up",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String fullName = fullNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword()).trim();

        if (fullName.isEmpty() || address.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DataStorage.getInstance().addUser(new DataStorage.User(
                    username,
                    password,
                    UserRole.CUSTOMER,
                    fullName,
                    address,
                    phone
            ));
            JOptionPane.showMessageDialog(mainPanel, "Account created successfully. You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(mainPanel, exception.getMessage(), "Sign Up Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        DataStorage.User user = DataStorage.getInstance().validateUser(username, password);

        if (user != null) {
            proceedToMainMenu(user);
        } else {
            JOptionPane.showMessageDialog(mainPanel, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void proceedToMainMenu(DataStorage.User user) {
        if (loginSuccessHandler != null) {
            loginSuccessHandler.accept(user);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        if (frame != null) {
            MainMenu mainMenu = new MainMenu(user);
            frame.setTitle("Warehouse Management System - Dashboard");
            frame.setContentPane(mainMenu.getMainPanel());
            frame.revalidate();
            frame.repaint();
        }
    }

    private void openMainMenu() {
        // Kept for backward compatibility if needed, but handleLogin is now preferred
        handleLogin();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public static void showLoginWindow() {
        ApplicationShell.showWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::showLoginWindow);
    }

    private Image loadBackgroundImage() {
        File imageFile = new File(LOGIN_BACKGROUND_PATH);
        if (!imageFile.exists()) {
            return null;
        }
        return new ImageIcon(imageFile.getAbsolutePath()).getImage();
    }

    private static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        private BackgroundPanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (backgroundImage == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }
}
