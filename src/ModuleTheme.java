import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class ModuleTheme {
    public static final Color PAGE_BACKGROUND = new Color(232, 238, 247);
    public static final Color SURFACE_BACKGROUND = new Color(13, 38, 76);
    public static final Color SURFACE_BORDER = new Color(33, 62, 108);
    public static final Color TITLE_COLOR = new Color(14, 40, 84);
    public static final Color SUBTITLE_COLOR = new Color(87, 108, 142);
    public static final Color SURFACE_TEXT = Color.WHITE;
    public static final Color SURFACE_MUTED_TEXT = new Color(209, 220, 241);
    public static final Color INPUT_BACKGROUND = new Color(243, 247, 252);
    public static final Color INPUT_TEXT = new Color(23, 37, 60);

    private ModuleTheme() {
    }

    public static JPanel createSurfacePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SURFACE_BORDER),
                new EmptyBorder(22, 22, 22, 22)
        ));
        return panel;
    }

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(SURFACE_TEXT);
        return label;
    }

    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(SURFACE_MUTED_TEXT);
        return label;
    }

    public static void styleInputField(JTextField field) {
        field.setPreferredSize(new Dimension(0, 34));
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(INPUT_TEXT);
        field.setCaretColor(INPUT_TEXT);
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(0, 34));
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setForeground(INPUT_TEXT);
    }

    public static void styleTextArea(JTextArea area) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBackground(new Color(22, 52, 99));
        area.setForeground(SURFACE_TEXT);
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        area.setCaretColor(SURFACE_TEXT);
    }
}
