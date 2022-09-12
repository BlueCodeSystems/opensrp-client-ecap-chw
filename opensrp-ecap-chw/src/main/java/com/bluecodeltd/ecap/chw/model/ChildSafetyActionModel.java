package com.bluecodeltd.ecap.chw.model;

public class ChildSafetyActionModel {
    private String safety_threats;
    private String safety_action;
    private String safety_protection;
    private String safety_plans;
    private String initial_date;
    private String base_entity_id;
    private String who;
    private String stateWhen;
    private String frequency;
    private String unique_id;

    public String getSafety_threats() {
        return safety_threats;
    }

    public void setSafety_threats(String safety_threats) {
        this.safety_threats = safety_threats;
    }

    public String getSafety_action() {
        return safety_action;
    }

    public void setSafety_action(String safety_action) {
        this.safety_action = safety_action;
    }

    public String getSafety_protection() {
        return safety_protection;
    }

    public void setSafety_protection(String safety_protection) {
        this.safety_protection = safety_protection;
    }

    public String getSafety_plans() {
        return safety_plans;
    }

    public void setSafety_plans(String safety_plans) {
        this.safety_plans = safety_plans;
    }

    public String getInitial_date() {
        return initial_date;
    }

    public void setInitial_date(String initial_date) {
        this.initial_date = initial_date;
    }

    public String getBase_entity_id() {
        return base_entity_id;
    }

    public void setBase_entity_id(String base_entity_id) {
        this.base_entity_id = base_entity_id;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getStateWhen() {
        return stateWhen;
    }

    public void setStateWhen(String stateWhen) {
        this.stateWhen = stateWhen;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }
}