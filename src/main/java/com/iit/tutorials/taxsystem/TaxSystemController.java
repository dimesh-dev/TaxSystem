package com.iit.tutorials.taxsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaxSystemController {
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> itemCodeCol;
    @FXML private TableColumn<Transaction, Double> internalPriceCol;
    @FXML private TableColumn<Transaction, Double> discountCol;
    @FXML private TableColumn<Transaction, Double> salePriceCol;
    @FXML private TableColumn<Transaction, Integer> quantityCol;
    @FXML private TableColumn<Transaction, Integer> checksumCol;
    @FXML private TableColumn<Transaction, Double> profitCol;
    @FXML private TableColumn<Transaction, Boolean> validCol;
    @FXML private Label summaryLabel;
    @FXML private TextField taxRateField;

    ObservableList<Transaction> transactions = FXCollections.observableArrayList(); // Changed to package-private

    @FXML
    public void initialize() {
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        internalPriceCol.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discount"));
        salePriceCol.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        checksumCol.setCellValueFactory(new PropertyValueFactory<>("checksum"));
        profitCol.setCellValueFactory(new PropertyValueFactory<>("profit"));
        validCol.setCellValueFactory(new PropertyValueFactory<>("valid"));

        transactionTable.setItems(transactions);
    }

    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Transaction File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try {
                List<Transaction> importedTransactions = importCSV(file);
                transactions.setAll(importedTransactions);
                validateTransactions();
                calculateProfits();
                updateSummary();
            } catch (IOException e) {
                showAlert("Error", "Failed to import file: " + e.getMessage());
            }
        }
    }

    private List<Transaction> importCSV(File file) throws IOException {
        List<Transaction> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip header row

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Transaction t = new Transaction(
                            parts[0],
                            Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]),
                            Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5])
                    );
                    result.add(t);
                }
            }
        }
        return result;
    }

    void validateTransactions() { // Changed to package-private
        for (Transaction t : transactions) {
            int calculatedChecksum = calculateChecksum(t);
            boolean checksumValid = calculatedChecksum == t.getChecksum();
            boolean codeValid = t.getItemCode().matches("^[a-zA-Z0-9_]+$");
            boolean priceValid = t.getInternalPrice() >= 0 && t.getSalePrice() >= 0;
            t.setValid(checksumValid && codeValid && priceValid);
        }
    }

    int calculateChecksum(Transaction t) { // Changed to package-private
        String line = String.format("%s,%s,%s,%s,%d",
                t.getItemCode(),
                String.valueOf(t.getInternalPrice()),
                String.valueOf(t.getDiscount()),
                String.valueOf(t.getSalePrice()),
                t.getQuantity()
        );

        int upper = 0, lower = 0, digits = 0;
        for (char c : line.toCharArray()) {
            if (Character.isUpperCase(c)) upper++;
            else if (Character.isLowerCase(c)) lower++;
            else if (Character.isDigit(c)) digits++;
        }
        return upper + lower + digits;
    }

    void calculateProfits() { // Changed to package-private
        for (Transaction t : transactions) {
            double profit = ((t.getSalePrice() * t.getQuantity()) - (t.getDiscount() * t.getQuantity())) -
                    (t.getInternalPrice() * t.getQuantity());
            t.setProfit(profit);
        }
    }

    @FXML
    private void handleUpdate() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No transaction selected");
            return;
        }

        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        dialog.setHeaderText("Update transaction details for DM SugarCraft");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField itemCodeField = new TextField(selected.getItemCode());
        TextField internalPriceField = new TextField(String.valueOf(selected.getInternalPrice()));
        TextField discountField = new TextField(String.valueOf(selected.getDiscount()));
        TextField salePriceField = new TextField(String.valueOf(selected.getSalePrice()));
        TextField quantityField = new TextField(String.valueOf(selected.getQuantity()));
        Label checksumLabel = new Label("Checksum will be recalculated");

        grid.add(new Label("Item Code:"), 0, 0);
        grid.add(itemCodeField, 1, 0);
        grid.add(new Label("Internal Price:"), 0, 1);
        grid.add(internalPriceField, 1, 1);
        grid.add(new Label("Discount:"), 0, 2);
        grid.add(discountField, 1, 2);
        grid.add(new Label("Sale Price:"), 0, 3);
        grid.add(salePriceField, 1, 3);
        grid.add(new Label("Quantity:"), 0, 4);
        grid.add(quantityField, 1, 4);
        grid.add(new Label("Checksum:"), 0, 5);
        grid.add(checksumLabel, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        itemCodeField.textProperty().addListener((obs, old, newValue) -> {
            try {
                double ip = Double.parseDouble(internalPriceField.getText());
                double d = Double.parseDouble(discountField.getText());
                double sp = Double.parseDouble(salePriceField.getText());
                int q = Integer.parseInt(quantityField.getText());
                saveButton.setDisable(newValue.trim().isEmpty() || ip < 0 || d < 0 || sp < 0 || q < 0);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
            }
        });
        internalPriceField.textProperty().addListener((obs, old, newValue) -> {
            try {
                double ip = Double.parseDouble(newValue);
                double d = Double.parseDouble(discountField.getText());
                double sp = Double.parseDouble(salePriceField.getText());
                int q = Integer.parseInt(quantityField.getText());
                saveButton.setDisable(itemCodeField.getText().trim().isEmpty() || ip < 0 || d < 0 || sp < 0 || q < 0);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
            }
        });
        discountField.textProperty().addListener((obs, old, newValue) -> {
            try {
                double ip = Double.parseDouble(internalPriceField.getText());
                double d = Double.parseDouble(newValue);
                double sp = Double.parseDouble(salePriceField.getText());
                int q = Integer.parseInt(quantityField.getText());
                saveButton.setDisable(itemCodeField.getText().trim().isEmpty() || ip < 0 || d < 0 || sp < 0 || q < 0);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
            }
        });
        salePriceField.textProperty().addListener((obs, old, newValue) -> {
            try {
                double ip = Double.parseDouble(internalPriceField.getText());
                double d = Double.parseDouble(discountField.getText());
                double sp = Double.parseDouble(newValue);
                int q = Integer.parseInt(quantityField.getText());
                saveButton.setDisable(itemCodeField.getText().trim().isEmpty() || ip < 0 || d < 0 || sp < 0 || q < 0);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
            }
        });
        quantityField.textProperty().addListener((obs, old, newValue) -> {
            try {
                double ip = Double.parseDouble(internalPriceField.getText());
                double d = Double.parseDouble(discountField.getText());
                double sp = Double.parseDouble(salePriceField.getText());
                int q = Integer.parseInt(newValue);
                saveButton.setDisable(itemCodeField.getText().trim().isEmpty() || ip < 0 || d < 0 || sp < 0 || q < 0);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String itemCode = itemCodeField.getText();
                    double internalPrice = Double.parseDouble(internalPriceField.getText());
                    double discount = Double.parseDouble(discountField.getText());
                    double salePrice = Double.parseDouble(salePriceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());

                    Transaction temp = new Transaction(itemCode, internalPrice, discount, salePrice, quantity, 0);
                    int newChecksum = calculateChecksum(temp);

                    return new Transaction(itemCode, internalPrice, discount, salePrice, quantity, newChecksum);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid input. Prices and quantity must be valid numbers.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newTransaction -> {
            selected.setItemCode(newTransaction.getItemCode());
            selected.setInternalPrice(newTransaction.getInternalPrice());
            selected.setDiscount(newTransaction.getDiscount());
            selected.setSalePrice(newTransaction.getSalePrice());
            selected.setQuantity(newTransaction.getQuantity());
            selected.setChecksum(newTransaction.getChecksum());

            double profit = ((selected.getSalePrice() * selected.getQuantity()) -
                    (selected.getDiscount() * selected.getQuantity())) -
                    (selected.getInternalPrice() * selected.getQuantity());
            selected.setProfit(profit);

            int calculatedChecksum = calculateChecksum(selected);
            boolean checksumValid = calculatedChecksum == selected.getChecksum();
            boolean codeValid = selected.getItemCode().matches("^[a-zA-Z0-9_]+$");
            boolean priceValid = selected.getInternalPrice() >= 0 && selected.getSalePrice() >= 0;
            selected.setValid(checksumValid && codeValid && priceValid);

            transactionTable.refresh();
            updateSummary();
        });
    }

    @FXML
    private void handleDelete() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            transactions.remove(selected);
            updateSummary();
        } else {
            showAlert("Error", "No transaction selected");
        }
    }

    @FXML
    private void handleDeleteZeroProfit() {
        transactions.removeIf(t -> t.getProfit() == 0);
        updateSummary();
    }

    @FXML
    private void handleCalculateTax() {
        try {
            double taxRate = Double.parseDouble(taxRateField.getText());
            if (taxRate < 0 || taxRate > 100) {
                showAlert("Error", "Tax rate must be between 0 and 100");
                return;
            }

            double totalProfit = transactions.stream()
                    .mapToDouble(Transaction::getProfit)
                    .sum();
            double finalTax = totalProfit * (taxRate / 100);

            showAlert("Tax Calculation",
                    String.format("Final Tax: %.2f (%.2f%% of %.2f)",
                            finalTax, taxRate, totalProfit));
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid tax rate format");
        }
    }

    @FXML
    private void handleExit() {
        ((Stage) transactionTable.getScene().getWindow()).close();
    }

    private void updateSummary() {
        long totalRecords = transactions.size();
        long validRecords = transactions.stream().filter(Transaction::isValid).count();
        summaryLabel.setText(String.format("Total Records: %d | Valid: %d | Invalid: %d",
                totalRecords, validRecords, totalRecords - validRecords));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}