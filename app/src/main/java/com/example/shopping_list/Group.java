package com.example.shopping_list;

import java.util.List;

public class Group {
    private String groupId;
    private String name;
    private String description;
    private List<String> members;
    private String creatorId;
    private String shoppingListId;  // New field for the shopping list ID

    // Default constructor for Firebase
    public Group() {}

    public Group(String groupId, String name, String description, List<String> members, String creatorId, String shoppingListId) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.members = members;
        this.creatorId = creatorId;
        this.shoppingListId = shoppingListId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getShoppingListId() {
        return shoppingListId;
    }
}
