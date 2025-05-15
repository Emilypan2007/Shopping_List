package com.example.shopping_list;

public class Item {
    private String name;
    private int quantity;
    private String remark;

    private String key; // This will store Firebase key

    public Item() {} // Needed for Firebase

    public Item(String name, int quantity, String remark) {
        this.name = name;
        this.quantity = quantity;
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRemark() {
        return remark;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }
}


