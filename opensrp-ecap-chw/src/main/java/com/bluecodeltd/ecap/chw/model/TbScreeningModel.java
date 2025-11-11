package com.bluecodeltd.ecap.chw.model;

public class TbScreeningModel {
    private String base_entity_id;
    private String unique_id;
    private String unique_tb_id;
    private String history_close_tb_contact;
    private String history_close_tb_contact_year;
    private String tb_symptoms_child_lt10;
    private String tb_symptoms_child_lt10_other;
    private String tb_symptoms_10plus;
    private String tb_symptoms_10plus_other;
    private String referred_for_tb_evaluation;
    private String tb_referral_comment;
    private String last_interacted_with;

    public String getBase_entity_id() { return base_entity_id; }
    public void setBase_entity_id(String base_entity_id) { this.base_entity_id = base_entity_id; }

    public String getUnique_id() { return unique_id; }
    public void setUnique_id(String unique_id) { this.unique_id = unique_id; }

    public String getUnique_tb_id() { return unique_tb_id; }
    public void setUnique_tb_id(String unique_tb_id) { this.unique_tb_id = unique_tb_id; }

    public String getHistory_close_tb_contact() { return history_close_tb_contact; }
    public void setHistory_close_tb_contact(String history_close_tb_contact) { this.history_close_tb_contact = history_close_tb_contact; }

    public String getHistory_close_tb_contact_year() { return history_close_tb_contact_year; }
    public void setHistory_close_tb_contact_year(String history_close_tb_contact_year) { this.history_close_tb_contact_year = history_close_tb_contact_year; }

    public String getTb_symptoms_child_lt10() { return tb_symptoms_child_lt10; }
    public void setTb_symptoms_child_lt10(String tb_symptoms_child_lt10) { this.tb_symptoms_child_lt10 = tb_symptoms_child_lt10; }

    public String getTb_symptoms_child_lt10_other() { return tb_symptoms_child_lt10_other; }
    public void setTb_symptoms_child_lt10_other(String tb_symptoms_child_lt10_other) { this.tb_symptoms_child_lt10_other = tb_symptoms_child_lt10_other; }

    public String getTb_symptoms_10plus() { return tb_symptoms_10plus; }
    public void setTb_symptoms_10plus(String tb_symptoms_10plus) { this.tb_symptoms_10plus = tb_symptoms_10plus; }

    public String getTb_symptoms_10plus_other() { return tb_symptoms_10plus_other; }
    public void setTb_symptoms_10plus_other(String tb_symptoms_10plus_other) { this.tb_symptoms_10plus_other = tb_symptoms_10plus_other; }

    public String getReferred_for_tb_evaluation() { return referred_for_tb_evaluation; }
    public void setReferred_for_tb_evaluation(String referred_for_tb_evaluation) { this.referred_for_tb_evaluation = referred_for_tb_evaluation; }

    public String getTb_referral_comment() { return tb_referral_comment; }
    public void setTb_referral_comment(String tb_referral_comment) { this.tb_referral_comment = tb_referral_comment; }

    public String getLast_interacted_with() { return last_interacted_with; }
    public void setLast_interacted_with(String last_interacted_with) { this.last_interacted_with = last_interacted_with; }
}
