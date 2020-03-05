package com.dineshneupane.ncovidroid.DataModel;

public class DataModel {
    private String country, lastUpdateDate, confirmedCases, totalDeaths, casesRecovered, region;

    public DataModel() {
    }

    public DataModel(String country, String lastUpdateDate, String confirmedCases, String totalDeaths, String casesRecovered, String region) {
        this.country = country;
        this.lastUpdateDate = lastUpdateDate;
        this.confirmedCases=confirmedCases;
        this.totalDeaths=totalDeaths;
        this.casesRecovered=casesRecovered;
        this.region=region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getConfirmedCases() {
        return confirmedCases;
    }

    public void setConfirmedCases(String confirmedCases) {
        this.confirmedCases = confirmedCases;
    }

    public String getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public String getCasesRecovered() {
        return casesRecovered;
    }

    public void setCasesRecovered(String casesRecovered) {
        this.casesRecovered = casesRecovered;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String casesRecovered) {
        this.region = region;
    }


}