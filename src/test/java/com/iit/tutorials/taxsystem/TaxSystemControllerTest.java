package com.iit.tutorials.taxsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaxSystemControllerTest {
    private TaxSystemController controller;
    private ObservableList<Transaction> transactions;

    @BeforeEach
    void setUp() {
        controller = new TaxSystemController();
        transactions = FXCollections.observableArrayList();
        controller.transactions = transactions;
    }

    private Transaction createTransaction(String itemCode, double internalPrice, double discount,
                                          double salePrice, int quantity, int checksum) {
        return new Transaction(itemCode, internalPrice, discount, salePrice, quantity, checksum);
    }

    @Test
    void testCalculateChecksum() {
        Transaction t = createTransaction("Chocolate_123", 10.0, 2.0, 15.0, 5, 0);
        int checksum = controller.calculateChecksum(t);
        assertEquals(21, checksum, "Checksum should be 21");
    }

    @Test
    void testValidateTransactions() {
        Transaction t = createTransaction("Chocolate_123", 10.0, 2.0, 15.0, 5, 21);
        transactions.add(t);
        controller.validateTransactions();
        assertTrue(t.isValid(), "Transaction should be valid with correct checksum and valid fields");
    }

    @Test
    void testCalculateProfits() {
        Transaction t = createTransaction("Chocolate_123", 10.0, 2.0, 15.0, 5, 21);
        transactions.add(t);
        controller.calculateProfits();
        assertEquals(15.0, t.getProfit(), 0.001, "Profit should be 15.0");
    }
}