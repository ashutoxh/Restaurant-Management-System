package data;

import model.MenuItem;

import java.io.*;
import java.util.*;

public class CsvFileHandler {
    private static CsvFileHandler instance;

    private CsvFileHandler() {}

    public static CsvFileHandler getInstance() {
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
                String[] values = line.split(","); // Assuming CSV values are comma-separated
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

}
