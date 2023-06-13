package com.bluecodeltd.ecap.chw.model;

public class AllChildrenHIVStatusModel {
    private String isHivPositive;
    private String birthdate;

    public AllChildrenHIVStatusModel(String isHivPositive, String birthdate) {
        this.isHivPositive = isHivPositive;
        this.birthdate = birthdate;
    }

    public String getIsHivPositive() {
        return isHivPositive;
    }

    public void setIsHivPositive(String isHivPositive) {
        this.isHivPositive = isHivPositive;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
