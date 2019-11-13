package net.dijitalbeyin.firma_rehberim.datamodel;

public class City {
    private int cityId;
    private String cityName;

    public City(int i, String str) {
        this.cityId = i;
        this.cityName = str;
    }

    public int getCityId() {
        return this.cityId;
    }

    public void setCityId(int i) {
        this.cityId = i;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String str) {
        this.cityName = str;
    }
}
