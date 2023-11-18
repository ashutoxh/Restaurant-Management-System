package model;

public class User {
    private String username;
    private String encryptedPassword;
    private String userType;

    public User(String username, String encryptedPassword, String userType) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}

