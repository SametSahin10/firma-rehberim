package net.dijitalbeyin.firma_rehberim;

public class Radio {
    private String category;
    private String categoryId;
    private int cityId;
    private int hit;
    private boolean isBeingBuffered;
    private boolean isLiked;
    private int neighbourhoodId;
    private int numOfOnlineListeners;
    private String radioIconUrl;
    private int radioId;
    private String radioName;
    private String shareableLink;
    private String streamLink;
    private int townId;
    private int userId;

    public Radio(int i, String str, int i2, int i3, int i4, String str2, int i5, String str3, String str4, String str5, String str6, int i6, int i7, boolean z, boolean z2) {
        this.radioId = i;
        this.cityId = i2;
        this.townId = i3;
        this.neighbourhoodId = i4;
        this.categoryId = str2;
        this.userId = i5;
        this.radioName = str;
        this.category = str3;
        this.radioIconUrl = str4;
        this.streamLink = str5;
        this.shareableLink = str6;
        this.hit = i6;
        this.numOfOnlineListeners = i7;
        this.isBeingBuffered = z;
        this.isLiked = z2;
    }

    public int getRadioId() {
        return this.radioId;
    }

    public void setRadioId(int i) {
        this.radioId = i;
    }

    public int getCityId() {
        return this.cityId;
    }

    public void setCityId(int i) {
        this.cityId = i;
    }

    public int getTownId() {
        return this.townId;
    }

    public void setTownId(int i) {
        this.townId = i;
    }

    public int getNeighbourhoodId() {
        return this.neighbourhoodId;
    }

    public void setNeighbourhoodId(int i) {
        this.neighbourhoodId = i;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String str) {
        this.categoryId = str;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int i) {
        this.userId = i;
    }

    public String getRadioName() {
        return this.radioName;
    }

    public void setRadioName(String str) {
        this.radioName = str;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String str) {
        this.category = str;
    }

    public String getRadioIconUrl() {
        return this.radioIconUrl;
    }

    public void setRadioIconUrl(String str) {
        this.radioIconUrl = str;
    }

    public String getStreamLink() {
        return this.streamLink;
    }

    public void setStreamLink(String str) {
        this.streamLink = str;
    }

    public String getShareableLink() {
        return this.shareableLink;
    }

    public void setShareableLink(String str) {
        this.shareableLink = str;
    }

    public int getHit() {
        return this.hit;
    }

    public void setHit(int i) {
        this.hit = i;
    }

    public int getNumOfOnlineListeners() {
        return this.numOfOnlineListeners;
    }

    public void setNumOfOnlineListeners(int i) {
        this.numOfOnlineListeners = i;
    }

    public boolean isBeingBuffered() {
        return this.isBeingBuffered;
    }

    public void setBeingBuffered(boolean z) {
        this.isBeingBuffered = z;
    }

    public boolean isLiked() {
        return this.isLiked;
    }

    public void setLiked(boolean z) {
        this.isLiked = z;
    }
}
