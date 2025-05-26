package com.example.shopping_list;

/**
 * Represents a user of the shopping list app.
 * Contains user details such as ID, full name, email, password, and reminder time.
 * Used for storing and retrieving user information from Firebase.
 *
 * @author Emily Panfilov
 * @version 1.0
 * @since 21/01/2025
 */
public class User {

    /** Unique identifier for the user */
    private String userId;

    /** Full name of the user */
    private String fullName;

    /** Email address of the user */
    private String email;

    /** User's password */
    private String password;

    /** Reminder time set by the user (in milliseconds as a String) */
    private String reminderTime;

    /**
     * Default constructor required for Firebase.
     */
    public User() {}

    /**
     * Parameterized constructor to initialize a User object.
     *
     * @param userId       The unique user ID
     * @param fullName     The user's full name
     * @param email        The user's email address
     * @param password     The user's password
     * @param reminderTime The time for a shopping reminder
     */
    public User(String userId, String fullName, String email, String password, String reminderTime) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.reminderTime = reminderTime;
    }

    /** @return the user's unique ID */
    public String getUserId() {
        return userId;
    }

    /** @param userId the user ID to set */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return the user's full name */
    public String getFullName() {
        return fullName;
    }

    /** @param fullName the full name to set */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** @return the user's email address */
    public String getEmail() {
        return email;
    }

    /** @param email the email address to set */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the user's password */
    public String getPassword() {
        return password;
    }

    /** @param password the password to set */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return the user's reminder time as a string */
    public String getReminderTime() {
        return reminderTime;
    }

    /** @param reminderTime the reminder time to set */
    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
