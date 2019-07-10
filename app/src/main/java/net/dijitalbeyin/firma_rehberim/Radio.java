package net.dijitalbeyin.firma_rehberim;

public class Radio {
    private int radioId;
    private String radioName;
    private String category;
    private String radioIconUrl;
    private String streamLink;
    private String shareableLink;
    private int hit;
    private int numOfOnlineListeners;
    private boolean isBeingBuffered;

    public Radio(int radioId, String radioName, String category, String radioIconUrl, String streamLink, String shareableLink, int hit, int numOfOnlineListeners, boolean isBeingBufferd) {
        this.radioId = radioId;
        this.radioName = radioName;
        this.category = category;
        this.radioIconUrl = radioIconUrl;
        this.streamLink = streamLink;
        this.shareableLink = shareableLink;
        this.hit = hit;
        this.numOfOnlineListeners = numOfOnlineListeners;
        this.isBeingBuffered = isBeingBufferd;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
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
}
