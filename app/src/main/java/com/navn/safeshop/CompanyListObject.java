package com.navn.safeshop;

public class CompanyListObject {
    private String companyName;
    private String companyAddress;
    private String companyDistance;

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyDistance() {
        return companyDistance;
    }
    public CompanyListObject(String companyName, String companyAddress) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;

    }
    public CompanyListObject(String companyName, String companyAddress, String companyDistance) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyDistance=companyDistance;
    }
    public void setCompanyDistance(String companyDistance) {
        this.companyDistance = companyDistance;
    }
}
