package com.iit.tutorials.taxsystem;

public class Transaction {
    private String itemCode;
    private double internalPrice;
    private double discount;
    private double salePrice;
    private int quantity;
    private int checksum;
    private double profit;
    private boolean valid;

    public Transaction(String itemCode, double internalPrice, double discount,
                       double salePrice, int quantity, int checksum) {
        this.itemCode = itemCode;
        this.internalPrice = internalPrice;
        this.discount = discount;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.checksum = checksum;
        this.profit = 0;
        this.valid = false;
    }

    // Getters and setters
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public double getInternalPrice() { return internalPrice; }
    public void setInternalPrice(double internalPrice) { this.internalPrice = internalPrice; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getChecksum() { return checksum; }
    public void setChecksum(int checksum) { this.checksum = checksum; }

    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
}