import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class ModulePanelFactory {
    private static final Dimension ACTION_BUTTON_SIZE = new Dimension(150, 40);

    private ModulePanelFactory() {
    }

    public static JPanel createModulePanel(String title, String description, String[] actions) {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(25, 42, 86));

        JLabel descriptionLabel = new JLabel("<html><div style='width:420px;'>" + description + "</div></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionLabel.setForeground(new Color(92, 107, 128));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new GridLayout(0, 2, 14, 14));
        actionsPanel.setOpaque(false);

        for (String action : actions) {
            JButton actionButton = new JButton(action);
            styleActionButton(actionButton, new Color(45, 125, 210));
            actionButton.addActionListener(event -> {
                String input = JOptionPane.showInputDialog(panel, "Enter details for " + action + ":", action, JOptionPane.QUESTION_MESSAGE);
                if (input != null && !input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(panel, action + " processed for: " + input, "Action Success", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actionsPanel.add(actionButton);
        }

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel infoLabel = new JLabel("Choose an action to continue.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(73, 80, 87));
        infoPanel.add(infoLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(actionsPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void styleActionButton(JButton button, Color background) {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setPreferredSize(ACTION_BUTTON_SIZE);
        button.setMinimumSize(ACTION_BUTTON_SIZE);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, ACTION_BUTTON_SIZE.height));
    }

    public static void styleContainedButtons(Container container, Color background) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button) {
                styleActionButton(button, background);
            } else if (component instanceof Container childContainer) {
                styleContainedButtons(childContainer, background);
            }
        }
    }
}
