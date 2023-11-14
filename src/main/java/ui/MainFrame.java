package ui;

import data.CsvFileHandler;
import model.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private CsvFileHandler csvFileHandler;
    private JLabel itemCountLabel;
    private String filePath = "src/main/resources/menu.csv";

    public MainFrame() {
        csvFileHandler = CsvFileHandler.getInstance();

        setTitle("Restaurant Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 255, 255)); // Set background color

        // Table setup with an additional column for type
        tableModel = new DefaultTableModel(new String[]{"Name", "Price", "Type"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return Double.class; // Price
                } else {
                    return String.class; // Name and Type
                }
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        styleTable();

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);

        // Sorting feature setup
        JPanel sortPanel = new JPanel();
        JComboBox<String> sortOptions = new JComboBox<>(new String[]{"Sort by Name", "Sort by Price", "Sort by Type"});
        JComboBox<String> sortOrderOptions = new JComboBox<>(new String[]{"Ascending", "Descending"});
        JButton sortButton = new JButton("Sort");

        sortPanel.add(new JLabel("Sort Field:"));
        sortPanel.add(sortOptions);
        sortPanel.add(new JLabel("Sort Order:"));
        sortPanel.add(sortOrderOptions);
        sortPanel.add(sortButton);

        sortButton.addActionListener(e -> sortTable(
                sortOptions.getSelectedIndex(),
                sortOrderOptions.getSelectedIndex() == 0 ? SortOrder.ASCENDING : SortOrder.DESCENDING
        ));
        add(sortPanel, BorderLayout.NORTH);

        JPanel itemCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemCountLabel = new JLabel();
        itemCountPanel.add(itemCountLabel);
        updateItemCount();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(sortPanel, BorderLayout.CENTER);
        topPanel.add(itemCountPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // CRUD operation buttons
        JPanel buttonPanel = new JPanel();
        addButton("Create", buttonPanel, e -> createOperation());
        addButton("Update", buttonPanel, e -> updateOperation());
        addButton("Delete", buttonPanel, e -> deleteOperation());
        add(buttonPanel, BorderLayout.SOUTH);

        // Read and display CSV data
        readOperation();
    }

    private void styleTable() {
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(200, 200, 200)); // Light gray grid lines
        table.getTableHeader().setBackground(new Color(150, 150, 150)); // Darker gray header
        table.getTableHeader().setForeground(Color.WHITE); // White text for header
    }

    private void addButton(String text, JPanel panel, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        panel.add(button);
    }

    // CRUD Operations
    private void createOperation() {
        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(10);
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Starter", "Main Course", "Dessert", "Beverage"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // Use GridLayout for structured layout
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Enter New Menu Item Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                String type = (String) typeField.getSelectedItem();

                MenuItem menuItem = new MenuItem(name, price, type);
                List<MenuItem> menuItems = csvFileHandler.readMenuItems(filePath);
                menuItems.add(menuItem);
                csvFileHandler.writeMenuItems(filePath, menuItems);
                updateItemCount();
                readOperation(); // Refresh data display
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid price.");
            }
        }
    }

    private void readOperation() {
        List<MenuItem> menuItems = csvFileHandler.readMenuItems(filePath);
        tableModel.setRowCount(0); // Clear existing data
        for (MenuItem item : menuItems) {
            tableModel.addRow(new Object[]{item.getName(), item.getPrice(), item.getType()});
        }
    }

    private void updateOperation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert the view index to model index
            int modelIndex = table.convertRowIndexToModel(selectedRow);

            // Capture the details of the selected item
            String name = (String) table.getModel().getValueAt(modelIndex, 0);
            double price = (Double) table.getModel().getValueAt(modelIndex, 1);
            String type = (String) table.getModel().getValueAt(modelIndex, 2);

            JTextField nameField = new JTextField(name, 10);
            JTextField priceField = new JTextField(String.valueOf(price), 10);
            JComboBox<String> typeField = new JComboBox<>(new String[]{"Starter", "Main Course", "Dessert", "Beverage"});
            typeField.setSelectedItem(type);

            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Type:"));
            panel.add(typeField);

            int result = JOptionPane.showConfirmDialog(null, panel,
                    "Edit Menu Item", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    List<MenuItem> menuItems = csvFileHandler.readMenuItems(filePath);
                    MenuItem itemToUpdate = menuItems.get(modelIndex);
                    itemToUpdate.setName(nameField.getText());
                    itemToUpdate.setPrice(Double.parseDouble(priceField.getText()));
                    itemToUpdate.setType((String) typeField.getSelectedItem());
                    csvFileHandler.writeMenuItems(filePath, menuItems);
                    updateItemCount();
                    readOperation(); // Refresh data display
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid price.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an item to update.");
        }
    }

    private void deleteOperation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this item?", "Delete Item", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    List<MenuItem> menuItems = csvFileHandler.readMenuItems(filePath);
                    // Convert the view index to the model index
                    int modelIndex = table.convertRowIndexToModel(selectedRow);
                    // Remove the item from the list
                    menuItems.remove(modelIndex);
                    // Write the updated list back to the CSV
                    csvFileHandler.writeMenuItems(filePath, menuItems);
                    // Refresh the table display
                    updateItemCount();
                    readOperation();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error occurred while deleting the item.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an item to delete.");
        }
    }


    private void sortTable(int sortOption, SortOrder sortOrder) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Ensure that price values are sorted numerically
        sorter.setComparator(1, Comparator.comparingDouble(value -> {
            if (value instanceof Double) {
                return (Double) value;
            }
            return Double.parseDouble(value.toString()); // Convert string to double for sorting
        }));

        // Determine the column to sort by
        int columnIndex;
        switch (sortOption) {
            case 0: columnIndex = 0; break; // Name
            case 1: columnIndex = 1; break; // Price
            case 2: columnIndex = 2; break; // Type
            default: return;
        }

        sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, sortOrder)));
    }

    private void updateItemCount() {
        List<MenuItem> menuItems = csvFileHandler.readMenuItems(filePath);
        Map<String, Long> countByType = menuItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getType, Collectors.counting()));

        long totalCount = menuItems.size(); // Total count of all menu items

        StringBuilder countsText = new StringBuilder("Total: " + totalCount + "\t\t\t");
        countByType.forEach((type, count) -> countsText.append(type).append(": ").append(count).append("\t\t\t"));

        itemCountLabel.setText(countsText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
