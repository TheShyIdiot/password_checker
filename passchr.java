import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class passchr extends JFrame {

    private JTextField passwordField;

    public passchr() {
        // Set frame properties
        setTitle("Password Strength Checker");
        setSize(600, 500); // Increased height for additional button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel with custom background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Create a gradient background
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 240, 240), 0, getHeight(), new Color(220, 220, 220));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add welcome label
        JLabel welcomeLabel = new JLabel("Welcome to Password Strength Checker", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Sans Serif", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        // Add instructions
        JLabel instructionsLabel = new JLabel("Enter your password to check its strength", JLabel.CENTER);
        instructionsLabel.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        instructionsLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        panel.add(instructionsLabel, gbc);

        // Create a container for input field and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setOpaque(false);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(inputPanel, gbc);

        // Add text field for password input with neumorphism effect
        passwordField = new JTextField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createEmptyBorder());
        passwordField.setOpaque(true);
        passwordField.setBackground(new Color(255, 255, 255)); // White background
        passwordField.setForeground(Color.BLACK);
        addNeumorphismEffect(passwordField, new Color(200, 220, 255), new Color(150, 180, 255)); // Light blue shadows
        GridBagConstraints fieldGBC = new GridBagConstraints();
        fieldGBC.insets = new Insets(5, 5, 5, 5);
        fieldGBC.gridx = 0;
        fieldGBC.gridy = 0;
        inputPanel.add(passwordField, fieldGBC);

        // Add check button for password strength with neumorphism effect
        JButton checkStrengthButton = new JButton("Check Password Strength");
        checkStrengthButton.setPreferredSize(new Dimension(200, 40));
        checkStrengthButton.setFont(new Font("Sans Serif", Font.BOLD, 14));
        checkStrengthButton.setBackground(new Color(255, 255, 255)); // White background
        checkStrengthButton.setForeground(Color.BLACK);
        checkStrengthButton.setBorder(BorderFactory.createEmptyBorder());
        checkStrengthButton.setFocusPainted(false);
        checkStrengthButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addNeumorphismEffect(checkStrengthButton, new Color(200, 220, 255), new Color(150, 180, 255)); // Light blue shadows
        GridBagConstraints buttonGBC = new GridBagConstraints();
        buttonGBC.insets = new Insets(5, 5, 5, 5);
        buttonGBC.gridx = 1;
        buttonGBC.gridy = 0;
        inputPanel.add(checkStrengthButton, buttonGBC);

        // Add action listener for strength check button
        checkStrengthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = passwordField.getText();
                int strength = checkPasswordStrength(password);
                boolean isCommon = isCommonPassword(password);
                String message = isCommon ? "This password is too common and has a strength of 0/10."
                        : "Password strength: " + strength + "/10";
                JOptionPane.showMessageDialog(null, message);
            }
        });

        // Add button to check if password is pwned
        JButton checkPwnedButton = new JButton("Check If Pwned ");
        checkPwnedButton.setPreferredSize(new Dimension(200, 40));
        checkPwnedButton.setFont(new Font("Sans Serif", Font.BOLD, 14));
        checkPwnedButton.setBackground(new Color(255, 255, 255)); // White background
        checkPwnedButton.setForeground(Color.BLACK);
        checkPwnedButton.setBorder(BorderFactory.createEmptyBorder());
        checkPwnedButton.setFocusPainted(false);
        checkPwnedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addNeumorphismEffect(checkPwnedButton, new Color(200, 220, 255), new Color(150, 180, 255)); // Light blue shadows
        GridBagConstraints pwnedButtonGBC = new GridBagConstraints();
        pwnedButtonGBC.insets = new Insets(10, 10, 10, 10);
        pwnedButtonGBC.gridx = 0;
        pwnedButtonGBC.gridy = 3;
        pwnedButtonGBC.gridwidth = 2;
        panel.add(checkPwnedButton, pwnedButtonGBC);

        // Add action listener for pwned check button
        checkPwnedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = passwordField.getText();
                boolean isPwned = isCommonPassword(password);
                String message = isPwned ? "This password has been pwned!" : "This password has not been pwned.";
                JOptionPane.showMessageDialog(null, message);
            }
        });

        // Add panel to frame
        add(panel);

        // Make the frame visible
        setVisible(true);
    }

    private void addNeumorphismEffect(JComponent component, Color shadowLight, Color shadowDark) {
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(shadowDark, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        component.setOpaque(true);
        component.setBackground(Color.WHITE);
        component.setForeground(Color.BLACK);
    }

    private int checkPasswordStrength(String password) {
        // Return 0 if password is common
        if (isCommonPassword(password)) {
            return 0;
        }

        int score = 0;

        // Check password length
        int n = password.length();
        if (n >= 10 && n <= 12) {
            score = 1;
        }

        // Check character types
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char b : password.toCharArray()) {
            if (Character.isUpperCase(b)) hasUpper = true;
            if (Character.isLowerCase(b)) hasLower = true;
            if (Character.isDigit(b)) hasDigit = true;
        }

        if (hasUpper && hasLower && hasDigit) {
            score += 3; // Increase score for complexity
        } else if ((hasUpper || hasLower) && hasDigit) {
            score += 2;
        }

        return score;
    }

    public static boolean isCommonPassword(String password) {
        try {
            // Hash the password using SHA-1
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String hashString = bytesToHex(hash).toUpperCase();

            // Send a request to the Pwned Passwords API
            URL url = new URL("https://api.pwnedpasswords.com/range/" + hashString.substring(0, 5));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                // Check if the hash appears in the response
                if (line.startsWith(hashString.substring(5))) {
                    return true; // Password is common
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Password is not common
    }

    // Helper method to convert byte array to hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Create and display the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new passchr().setVisible(true);
            }
        });
    }
}