package data;

import model.MenuItem;
import model.User;

import java.io.*;
import java.util.*;

public class CsvFileHandler {
    private static CsvFileHandler instance;

    private CsvFileHandler() {}

    public static synchronized CsvFileHandler getInstance() {
        if (instance == null) {
            instance = new CsvFileHandler();
        }
        return instance;
    }

    public List<MenuItem> readMenuItems(String filePath) {
        List<MenuItem> menuItems = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String name = values[0];
                    double price = Double.parseDouble(values[1]);
                    String type = values[2];
                    menuItems.add(new MenuItem(name, price, type));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }
        return menuItems;
    }

    public void writeMenuItems(String filePath, List<MenuItem> menuItems) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (MenuItem item : menuItems) {
                pw.println(item.getName() + "," + item.getPrice() + "," + item.getType());
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    // Method to read user details from a CSV file
    public List<User> readUserDetails(String filePath) {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String username = values[0];
                    String encryptedPassword = values[1];
                    String userType = values[2];
                    users.add(new User(username, encryptedPassword, userType));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Method to write user details to a CSV file
    public void writeUserDetails(String filePath, List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (User user : users) {
                pw.println(user.getUsername() + "," + user.getEncryptedPassword() + "," + user.getUserType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String encryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

}
