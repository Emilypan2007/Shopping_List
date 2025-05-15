package com.example.shopping_list;

public class User {
    /**
     * @author Emily panfilov
     * @version 1.0
     * @since 21/1/25
     */
    private String userId; //המזהה הייחודי של המשתמש
    private String fullName; //השם המלא של המשתמש
    private String email; // האימייל של המשתמש
    private String password; // הסיסמה של המשתמש
    private String reminderTime; // זמן התזכורת שהמשתמש הגדיר

    public User() {} // בנאי ריק לעצם User

    public User(String userId, String fullName, String email,String password,String reminderTime) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.reminderTime = reminderTime;
    } //

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
