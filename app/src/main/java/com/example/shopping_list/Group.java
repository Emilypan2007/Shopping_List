package com.example.shopping_list;

import java.util.List;

/**
 * Represents a group in the shopping list application.
 * Each group has an ID, name, description, list of members, a creator, and a shopping list ID.
 */
public class Group {
    private String groupId;
    private String name;
    private String description;
    private List<String> members;
    private String creatorId;
    private String shoppingListId;  // ID for the associated shopping list

    /**
     * Default no-argument constructor required for Firebase deserialization.
     */
    public Group() {}

    /**
     * Constructs a Group object with the specified details.
     *
     * @param groupId         The unique ID of the group.
     * @param name            The name of the group.
     * @param description     A short description of the group.
     * @param members         A list of user IDs who are members of the group.
     * @param creatorId       The ID of the user who created the group.
     * @param shoppingListId  The ID of the shopping list associated with this group.
     */
    public Group(String groupId, String name, String description, List<String> members, String creatorId, String shoppingListId) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.members = members;
        this.creatorId = creatorId;
        this.shoppingListId = shoppingListId;
    }

    /**
     * Returns the group ID.
     * @return The unique ID of the group.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Returns the group name.
     * @return The name of the group.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the group's description.
     * @return The description of the group.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the list of member user IDs in the group.
     * @return A list of user IDs.
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * Returns the ID of the group creator.
     * @return The creator's user ID.
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * Returns the shopping list ID associated with the group.
     * @return The shopping list ID.
     */
    public String getShoppingListId() {
        return shoppingListId;
    }
}
