import javax.swing.*;
import java.awt.*;

public class ApplicationShell {
    private static final String LOGIN_PAGE = "login";
    private static final String DASHBOARD_PAGE = "dashboard";

    private final JFrame frame;
    private final JPanel rootPanel;
    private final CardLayout rootLayout;
    private final LoginForm loginForm;
    private JPanel dashboardPanel;

    public ApplicationShell() {
        frame = new JFrame("Warehouse Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

        loginForm = new LoginForm(this::showDashboard);
        rootPanel.add(loginForm.getMainPanel(), LOGIN_PAGE);

        frame.setContentPane(rootPanel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(1100, 760));
    }

    private void showDashboard(DataStorage.User user) {
        if (dashboardPanel != null) {
            rootPanel.remove(dashboardPanel);
        }

        if (user.role == UserRole.CUSTOMER) {
            dashboardPanel = new CustomerMainMenu(user, this::showLogin).getMainPanel();
        } else {
            dashboardPanel = new MainMenu(user, this::showLogin).getMainPanel();
        }

        rootPanel.add(dashboardPanel, DASHBOARD_PAGE);
        frame.setTitle("Warehouse Management System - Dashboard");
        rootLayout.show(rootPanel, DASHBOARD_PAGE);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    private void showLogin() {
        frame.setTitle("Warehouse Management System");
        rootLayout.show(rootPanel, LOGIN_PAGE);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    private void show() {
        rootLayout.show(rootPanel, LOGIN_PAGE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showWindow() {
        SwingUtilities.invokeLater(() -> new ApplicationShell().show());
    }
}
