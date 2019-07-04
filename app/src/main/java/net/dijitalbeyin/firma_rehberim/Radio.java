package net.dijitalbeyin.firma_rehberim;

import android.graphics.Bitmap;

public class Radio {
    private int radioId;
    private String radioName;
    private String category;
    private String radioIconLink;
    private String streamLink;
    private String shareableLink;
    private int hit;
    private int numOfOnlineListeners;

    public Radio(int radioId, String radioName, String category, String radioIconLink, String streamLink, String shareableLink, int hit, int numOfOnlineListeners) {
        this.radioId = radioId;
        this.radioName = radioName;
        this.category = category;
        this.radioIconLink = radioIconLink;
        this.streamLink = streamLink;
        this.shareableLink = shareableLink;
        this.hit = hit;
        this.numOfOnlineListeners = numOfOnlineListeners;
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

    public String getRadioIconLink() {
        return radioIconLink;
    }

    public void setRadioIconLink(String radioIconLink) {
        this.radioIconLink = radioIconLink;
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
}
