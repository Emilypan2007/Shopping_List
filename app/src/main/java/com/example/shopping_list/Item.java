package com.example.shopping_list;

/**
 * Represents an item in the shopping list.
 * Each item includes a name, quantity, optional remark, and a Firebase key.
 */
public class Item {
    private String name;
    private int quantity;
    private String remark;
    private String key; // This will store the Firebase key

    /**
     * Default constructor required for Firebase deserialization.
     */
    public Item() {}

    /**
     * Constructs an Item with the specified name, quantity, and remark.
     *
     * @param name    Name of the item.
     * @param quantity Quantity of the item.
     * @param remark  Optional remark about the item.
     */
    public Item(String name, int quantity, String remark) {
        this.name = name;
        this.quantity = quantity;
        this.remark = remark;
    }

    /**
     * Gets the name of the item.
     *
     * @return Item name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     *
     * @param name New name of the item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the quantity of the item.
     *
     * @return Item quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the item.
     *
     * @param quantity New quantity of the item.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the remark associated with the item.
     *
     * @return Item remark.
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Gets the Firebase key of the item.
     *
     * @return Firebase key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the Firebase key of the item.
     *
     * @param key Firebase key.
     */
    public void setKey(String key) {
        this.key = key;
    }
}
