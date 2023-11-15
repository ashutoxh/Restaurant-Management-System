package ui;

import data.CsvFileHandler;
import model.User;
import utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean isAuthenticated = false;
    private String userType;
    private CsvFileHandler csvFileHandler;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        csvFileHandler = CsvFileHandler.getInstance();

        setLayout(new GridLayout(3, 2, 10, 10));

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        buttonPanel.setAlignmentX(Component.BOTTOM_ALIGNMENT);
        add(buttonPanel);

        loginButton.addActionListener(e -> attemptLogin());
        add(loginButton);

        pack();
        setLocationRelativeTo(parent);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        List<User> users = csvFileHandler.readUserDetails(Constants.DATABASE_FILE_PATH);

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getEncryptedPassword().equals(csvFileHandler.encryptPassword(password))) {
                userType = user.getUserType();
                System.out.println("User name in attemptLogin: " + user.getUsername());
                System.out.println("User type in attemptLogin: " + userType);
                isAuthenticated = true;
                break;
            }
        }

        if (isAuthenticated) {
            dispose(); // Close the dialog properly
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            // Clear the password field for retry
            passwordField.setText("");
        }
    }

    public String getUserType() {
        return userType;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}



