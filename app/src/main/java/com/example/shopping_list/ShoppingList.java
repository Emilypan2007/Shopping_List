package com.example.shopping_list;

import java.util.List;

/**
 * Represents a shopping list associated with a specific group.
 * Contains a list of items and unique identifiers for the list and its group.
 */
public class ShoppingList {
    private String shoppingListId;
    private String groupId;
    private List<Item> items;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public ShoppingList() {
        // Default constructor required for Firebase
    }

    /**
     * Constructs a ShoppingList object with the specified ID, group ID, and items.
     *
     * @param shoppingListId Unique identifier for the shopping list.
     * @param groupId        Identifier of the group to which the list belongs.
     * @param items          List of items in the shopping list.
     */
    public ShoppingList(String shoppingListId, String groupId, List<Item> items) {
        this.shoppingListId = shoppingListId;
        this.groupId = groupId;
        this.items = items;
    }

    /**
     * Gets the shopping list ID.
     *
     * @return The unique identifier of the shopping list.
     */
    public String getShoppingListId() {
        return shoppingListId;
    }

    /**
     * Sets the shopping list ID.
     *
     * @param shoppingListId The new shopping list ID.
     */
    public void setShoppingListId(String shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    /**
     * Gets the group ID associated with this shopping list.
     *
     * @return The group ID.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the group ID for this shopping list.
     *
     * @param groupId The group ID to set.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the list of items in this shopping list.
     *
     * @return A list of Item objects.
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Sets the list of items for this shopping list.
     *
     * @param items A list of Item objects.
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }
}
