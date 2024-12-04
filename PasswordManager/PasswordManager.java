import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordManager {

    // Encryption key for simplicity (not secure for production use)
    private static final String ENCRYPTION_KEY = "simplekey12345678";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordManager::createAndShowGUI);
    }

    // GUI creation method
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Password Generator and Checker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel lengthLabel = new JLabel("Enter desired password length (minimum 6):");
        JTextField lengthField = new JTextField();
        JButton generateButton = new JButton("Generate Password");
        JLabel generatedPasswordLabel = new JLabel("Generated Password: ");
        JTextField passwordField = new JTextField();
        JButton saveButton = new JButton("Save Password");
        JButton checkButton = new JButton("Check Password Strength");
        JLabel resultLabel = new JLabel("Result: ");

        // Add components to frame
        frame.add(lengthLabel);
        frame.add(lengthField);
        frame.add(generateButton);
        frame.add(generatedPasswordLabel);
        frame.add(passwordField);
        frame.add(saveButton);
        frame.add(checkButton);
        frame.add(resultLabel);

        // Generate password action
        generateButton.addActionListener(e -> {
            try {
                int length = Integer.parseInt(lengthField.getText());
                if (length < 6) throw new IllegalArgumentException();
                String generatedPassword = generatePassword(length);
                generatedPasswordLabel.setText("Generated Password: " + generatedPassword);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter a number >= 6.");
            }
        });

        // Save password action
        saveButton.addActionListener(e -> {
            String password = passwordField.getText();
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password field is empty!");
            } else {
                savePassword(password);
                JOptionPane.showMessageDialog(frame, "Password saved successfully!");
            }
        });

        // Check password action
        checkButton.addActionListener(e -> {
            String password = passwordField.getText();
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password field is empty!");
            } else {
                boolean isStrong = isPasswordStrong(password);
                resultLabel.setText("Result: Password is " + (isStrong ? "Strong" : "Weak"));
            }
        });

        frame.setVisible(true);
    }

    // Method to generate a random password
    private static String generatePassword(int length) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_+=<>?";
        String allChars = upperCase + lowerCase + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest of the password
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return shuffleString(password.toString());
    }

    // Helper method to shuffle a string
    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        SecureRandom random = new SecureRandom();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    // Method to check password strength
    private static boolean isPasswordStrong(String password) {
        if (password.length() < 6) return false;

        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if ("!@#$%^&*()-_+=<>?".contains(String.valueOf(ch))) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    // Method to save password securely
    private static void savePassword(String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("passwords.txt", true))) {
            String encryptedPassword = encrypt(password);
            writer.write(encryptedPassword);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Simple encryption method
    private static String encrypt(String data) {
        byte[] key = ENCRYPTION_KEY.getBytes();
        byte[] dataBytes = data.getBytes();
        byte[] encrypted = new byte[dataBytes.length];

        for (int i = 0; i < dataBytes.length; i++) {
            encrypted[i] = (byte) (dataBytes[i] ^ key[i % key.length]);
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Simple decryption method
    private static String decrypt(String encryptedData) {
        byte[] key = ENCRYPTION_KEY.getBytes();
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = new byte[encryptedBytes.length];

        for (int i = 0; i < encryptedBytes.length; i++) {
            decrypted[i] = (byte) (encryptedBytes[i] ^ key[i % key.length]);
        }

        return new String(decrypted);
    }
}
