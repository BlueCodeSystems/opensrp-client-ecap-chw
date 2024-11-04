package com.bluecodeltd.ecap.chw.model;

public class PmtctChildOutcomeModel {
    private String base_entity_id;
    private String pmtct_id;
    private String unique_id;
    private String child_outcome;


    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getPmtct_id() {
        return pmtct_id;
    }

    public void setPmtct_id(String pmtct_id) {
        this.pmtct_id = pmtct_id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getChild_outcome() {
        return child_outcome;
    }

    public void setChild_outcome(String child_outcome) {
        this.child_outcome = child_outcome;
    }
}
