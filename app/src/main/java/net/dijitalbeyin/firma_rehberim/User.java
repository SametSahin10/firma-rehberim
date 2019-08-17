package net.dijitalbeyin.firma_rehberim;

public class User {
    private String userName;
    private String userPhotoLink;
    private String userId;
    private String authoritativeName;

    public User(String userName, String userPhotoLink, String userId, String authoritativeName) {
        this.userName = userName;
        this.userPhotoLink = userPhotoLink;
        this.userId = userId;
        this.authoritativeName = authoritativeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoLink() {
        return userPhotoLink;
    }

    public void setUserPhotoLink(String userPhotoLink) {
        this.userPhotoLink = userPhotoLink;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthoritativeName() {
        return authoritativeName;
    }

    public void setAuthoritativeName(String authoritativeName) {
        this.authoritativeName = authoritativeName;
    }
}
