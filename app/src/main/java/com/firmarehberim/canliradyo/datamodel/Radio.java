package com.firmarehberim.canliradyo.datamodel;

public class Radio {
    private int radioId;
    private int cityId;
    private int townId;
    private int neighbourhoodId;
    private String radioIconUrl;
    private String shareableLink;
    private String radioName;
    private String streamLink;
    private int hit;
    private String categoryId;
    private int userId;
    private String category;
    private int numOfOnlineListeners;
    private boolean isLiked;
    private boolean isBeingBuffered;
    private boolean isPlaying;

    public Radio(int radioId,
                 int cityId,
                 int townId,
                 int neighbourhoodId,
                 String radioIconUrl,
                 String shareableLink,
                 String radioName,
                 String streamLink,
                 int hit,
                 String categoryId,
                 int userId,
                 String category,
                 int numOfOnlineListeners,
                 boolean isLiked,
                 boolean isBeingBuffered,
                 boolean isPlaying) {
        this.radioId = radioId;
        this.cityId = cityId;
        this.townId = townId;
        this.neighbourhoodId = neighbourhoodId;
        this.radioIconUrl = radioIconUrl;
        this.shareableLink = shareableLink;
        this.radioName = radioName;
        this.streamLink = streamLink;
        this.hit = hit;
        this.categoryId = categoryId;
        this.userId = userId;
        this.category = category;
        this.numOfOnlineListeners = numOfOnlineListeners;
        this.isLiked = isLiked;
        this.isBeingBuffered = isBeingBuffered;
        this.isPlaying = isPlaying;
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

    public String getRadioIconUrl() {
        return radioIconUrl;
    }

    public void setRadioIconUrl(String radioIconUrl) {
        this.radioIconUrl = radioIconUrl;
    }

    public String getShareableLink() {
        return shareableLink;
    }

    public void setShareableLink(String shareableLink) {
        this.shareableLink = shareableLink;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public String getStreamLink() {
        return streamLink;
    }

    public void setStreamLink(String streamLink) {
        this.streamLink = streamLink;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumOfOnlineListeners() {
        return numOfOnlineListeners;
    }

    public void setNumOfOnlineListeners(int numOfOnlineListeners) {
        this.numOfOnlineListeners = numOfOnlineListeners;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isBeingBuffered() {
        return isBeingBuffered;
    }

    public void setBeingBuffered(boolean beingBuffered) {
        isBeingBuffered = beingBuffered;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
