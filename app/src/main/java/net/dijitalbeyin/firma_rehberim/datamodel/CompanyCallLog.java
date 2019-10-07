package net.dijitalbeyin.firma_rehberim.datamodel;

public class CompanyCallLog {
    private String companyName;
    private String authoritativeName;
    private String callType;
    private String dateInfo;

    public CompanyCallLog(String companyName, String authoritativeName, String callType, String dateInfo) {
        this.companyName = companyName;
        this.authoritativeName = authoritativeName;
        this.callType = callType;
        this.dateInfo = dateInfo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAuthoritativeName() {
        return authoritativeName;
    }

    public void setAuthoritativeName(String authoritativeName) {
        this.authoritativeName = authoritativeName;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }
}
