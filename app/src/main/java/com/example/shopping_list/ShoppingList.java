package com.example.shopping_list;

import java.util.List;

public class ShoppingList {
    private String shoppingListId;
    private String groupId;
    private List<Item> items;

    public ShoppingList() {
        // Default constructor required for Firebase
    }

    public ShoppingList(String shoppingListId, String groupId, List<Item> items) {
        this.shoppingListId = shoppingListId;
        this.groupId = groupId;
        this.items = items;
    }

    public String getShoppingListId() {
        return shoppingListId;
    }

    public void setShoppingListId(String shoppingListId) {
        this.shoppingListId = shoppingListId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}

