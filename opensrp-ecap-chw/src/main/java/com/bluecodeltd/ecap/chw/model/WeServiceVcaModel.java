package com.bluecodeltd.ecap.chw.model;

public class WeServiceVcaModel {

    private String base_entity_id;
    private String household_id;
    private String vca_id;
    private String date_joined_we;

    public String getHousehold_id() {
        return household_id;
    }

    public void setHousehold_id(String household_id) {
        this.household_id = household_id;
    }

    public String getVCA_id() {
        return vca_id;
    }

    public void setVCA_id(String caregiver_id) {
        this.vca_id = caregiver_id;
    }

    public String getDate_joined_we() {
        return date_joined_we;
    }

    public void setDate_joined_we(String date_joined_we) {
        this.date_joined_we = date_joined_we;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

}
