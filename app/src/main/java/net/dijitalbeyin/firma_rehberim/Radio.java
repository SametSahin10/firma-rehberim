package net.dijitalbeyin.firma_rehberim;

public class Radio {
    private int radioId;
    private int cityId;
    private int townId; //ilceId
    private int neighbourhoodId; //mahalleId
    private String categoryId;
    private int userId;
    private String radioName;
    private String category;
    private String radioIconUrl;
    private String streamLink;
    private String shareableLink;
    private int hit;
    private int numOfOnlineListeners;
    private boolean isBeingBuffered;
    private boolean isLiked;

    public Radio(int radioId,
                 String radioName,
                 int cityId,
                 int townId,
                 int neighbourhoodId,
                 String categoryId,
                 int userId,
                 String category,
                 String radioIconUrl,
                 String streamLink,
                 String shareableLink,
                 int hit,
                 int numOfOnlineListeners,
                 boolean isBeingBuffered,
                 boolean isLiked) {
        this.radioId = radioId;
        this.cityId = cityId;
        this.townId = townId;
        this.neighbourhoodId = neighbourhoodId;
        this.categoryId = categoryId;
        this.userId = userId;
        this.radioName = radioName;
        this.category = category;
        this.radioIconUrl = radioIconUrl;
        this.streamLink = streamLink;
        this.shareableLink = shareableLink;
        this.hit = hit;
        this.numOfOnlineListeners = numOfOnlineListeners;
        this.isBeingBuffered = isBeingBuffered;
        this.isLiked = isLiked;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getTownId() {
        return townId;
    }

    public void setTownId(int townId) {
        this.townId = townId;
    }

    public int getNeighbourhoodId() {
        return neighbourhoodId;
    }

    public void setNeighbourhoodId(int neighbourhoodId) {
        this.neighbourhoodId = neighbourhoodId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRadioIconUrl() {
        return radioIconUrl;
    }

    public void setRadioIconUrl(String radioIconUrl) {
        this.radioIconUrl = radioIconUrl;
    }

    public String getStreamLink() {
        return streamLink;
    }

    public void setStreamLink(String streamLink) {
        this.streamLink = streamLink;
    }

    public String getShareableLink() {
        return shareableLink;
    }

    public void setShareableLink(String shareableLink) {
        this.shareableLink = shareableLink;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getNumOfOnlineListeners() {
        return numOfOnlineListeners;
    }

    public void setNumOfOnlineListeners(int numOfOnlineListeners) {
        this.numOfOnlineListeners = numOfOnlineListeners;
    }

    public boolean isBeingBuffered() {
        return isBeingBuffered;
    }

    public void setBeingBuffered(boolean beingBuffered) {
        isBeingBuffered = beingBuffered;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
