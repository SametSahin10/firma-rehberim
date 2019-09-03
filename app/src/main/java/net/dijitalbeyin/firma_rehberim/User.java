package net.dijitalbeyin.firma_rehberim;

public class User {
    private String userWebpageLink;
    private String userName;
    private String userPhotoLink;
    private boolean isVerified;
    private int match;
    private String authoritativeWebpageLink;
    private String userId;
    private String authoritativeName;

    public User(String userWebpageLink, String userName, String userPhotoLink, boolean isVerified, int match, String authoritativeWebpageLink, String userId, String authoritativeName) {
        this.userWebpageLink = userWebpageLink;
        this.userName = userName;
        this.userPhotoLink = userPhotoLink;
        this.isVerified = isVerified;
        this.match = match;
        this.authoritativeWebpageLink = authoritativeWebpageLink;
        this.userId = userId;
        this.authoritativeName = authoritativeName;
    }

    public String getUserWebpageLink() {
        return userWebpageLink;
    }

    public void setUserWebpageLink(String userWebpageLink) {
        this.userWebpageLink = userWebpageLink;
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }

    public String getAuthoritativeWebpageLink() {
        return authoritativeWebpageLink;
    }

    public void setAuthoritativeWebpageLink(String authoritativeWebpageLink) {
        this.authoritativeWebpageLink = authoritativeWebpageLink;
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
