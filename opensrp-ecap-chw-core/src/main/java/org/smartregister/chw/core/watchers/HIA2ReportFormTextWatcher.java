package org.smartregister.chw.core.watchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import java.util.HashMap;
import java.util.Map;

public class HIA2ReportFormTextWatcher implements TextWatcher {
    private static final Map<String, String[]> aggregateFieldsMap;

    static {
        aggregateFieldsMap = new HashMap<>();
        aggregateFieldsMap.put("total_preg_visit", new String[]{"newpreg_mama_visit", "oldpreg_mama_visit"});
        aggregateFieldsMap.put("total_F_visited", new String[]{"total_preg_visit", "pnc_visit"});
        aggregateFieldsMap.put("total_U5_visit", new String[]{"less1m_visit", "1m1yr_visit", "1yr5yr_visit"});
        aggregateFieldsMap.put("total_referral", new String[]{"F_referral_hf", "less1m_referral_hf", "1m1yr_referral_hf", "1yr5yr_referral_hf"});
        aggregateFieldsMap.put("total_birth_home", new String[]{"birth_home", "birth_home_healer", "birth_way_hf"});
        aggregateFieldsMap.put("10y14y_total_clients", new String[]{"10y14y_new_clients", "10y14y_return_clients", "10y14y_new_men_clients", "10y14y_return_men_clients"});
        aggregateFieldsMap.put("15y19y_total_clients", new String[]{"15y19y_new_clients", "15y19y_return_clients", "15y19y_new_men_clients", "15y19y_return_men_clients"});
        aggregateFieldsMap.put("20y24y_total_clients", new String[]{"20y24y_new_clients", "20y24y_return_clients", "20y24y_new_men_clients", "20y24y_return_men_clients"});
        aggregateFieldsMap.put("25_total_clients", new String[]{"25_new_clients", "25_return_clients", "25_new_men_clients", "25_return_men_clients"});
        aggregateFieldsMap.put("total_total_clients", new String[]{"total_new_clients", "total_return_clients", "total_new_men_clients", "total_return_men_clients"});
        aggregateFieldsMap.put("10y14y_total_pills", new String[]{"10y14y_pop", "10y14y_coc", "10y14y_emc"});
        aggregateFieldsMap.put("15y19y_total_pills", new String[]{"15y19y_pop", "15y19y_coc", "15y19y_emc"});
        aggregateFieldsMap.put("20y24y_total_pills", new String[]{"20y24y_pop", "20y24y_coc", "20y24y_emc"});
        aggregateFieldsMap.put("25_total_pills", new String[]{"25_pop", "25_coc", "25_emc"});
        aggregateFieldsMap.put("total_total_pills", new String[]{"total_pop", "total_coc", "total_emc"});
        aggregateFieldsMap.put("10y14y_total_condoms", new String[]{"10y14y_F_mcondom", "10y14y_F_fcondom", "10y14y_men_F_mcondom", "10y14y_men_F_fcondom"});
        aggregateFieldsMap.put("15y19y_total_condoms", new String[]{"15y19y_F_mcondom", "15y19y_F_fcondom", "15y19y_men_F_mcondom", "15y19y_men_F_fcondom"});
        aggregateFieldsMap.put("20y24y_total_condoms", new String[]{"20y24y_F_mcondom", "20y24y_F_fcondom", "20y24y_men_F_mcondom", "20y24y_men_F_fcondom"});
        aggregateFieldsMap.put("25_total_condoms", new String[]{"25_F_mcondom", "25_F_fcondom", "25_men_F_mcondom", "25_men_F_fcondom"});
        aggregateFieldsMap.put("total_total_condoms", new String[]{"total_F_mcondom", "total_F_fcondom", "total_men_F_mcondom", "total_men_F_fcondom"});
        aggregateFieldsMap.put("total_beads", new String[]{"10y14y_beads", "15y19y_beads", "20y24y_beads", "25_beads"});
        aggregateFieldsMap.put("total_cousel_ANC", new String[]{"10y14y_cousel_ANC", "15y19y_cousel_ANC", "20y24y_cousel_ANC", "25_cousel_ANC"});
        aggregateFieldsMap.put("total_cousel_delivery", new String[]{"10y14y_cousel_delivery", "15y19y_cousel_delivery", "20y24y_cousel_delivery", "25_cousel_delivery"});
        aggregateFieldsMap.put("total_cousel_PNC", new String[]{"10y14y_cousel_PNC", "15y19y_cousel_PNC", "20y24y_cousel_PNC", "25_cousel_PNC"});
        aggregateFieldsMap.put("total_fp_referral", new String[]{"10y14y_referral", "15y19y_referral", "20y24y_referral", "25_referral", "10y14y_men_referral", "15y19y_men_referral", "20y24y_men_referral", "25_men_referral"});
    }

    private static final Map<String, String> indicatorKeyMap;

    static {
        indicatorKeyMap = new HashMap<>();
        indicatorKeyMap.put("newpreg_mama_visit", "total_preg_visit");
        indicatorKeyMap.put("oldpreg_mama_visit", "total_preg_visit");
        indicatorKeyMap.put("total_preg_visit", "total_F_visited");
        indicatorKeyMap.put("pnc_visit", "total_F_visited");
        indicatorKeyMap.put("less1m_visit", "total_U5_visit");
        indicatorKeyMap.put("1m1yr_visit", "total_U5_visit");
        indicatorKeyMap.put("1yr5yr_visit", "total_U5_visit");
        indicatorKeyMap.put("F_referral_hf", "total_referral");
        indicatorKeyMap.put("less1m_referral_hf", "total_referral");
        indicatorKeyMap.put("1m1yr_referral_hf", "total_referral");
        indicatorKeyMap.put("1yr5yr_referral_hf", "total_referral");
        indicatorKeyMap.put("birth_home", "total_birth_home");
        indicatorKeyMap.put("birth_home_healer", "total_birth_home");
        indicatorKeyMap.put("birth_way_hf", "total_birth_home");
        indicatorKeyMap.put("10y14y_new_clients", "10y14y_total_clients");
        indicatorKeyMap.put("10y14y_return_clients", "10y14y_total_clients");
        indicatorKeyMap.put("10y14y_new_men_clients", "10y14y_total_clients");
        indicatorKeyMap.put("10y14y_return_men_clients", "10y14y_total_clients");
        indicatorKeyMap.put("15y19y_new_clients", "15y19y_total_clients");
        indicatorKeyMap.put("15y19y_return_clients", "15y19y_total_clients");
        indicatorKeyMap.put("15y19y_new_men_clients", "15y19y_total_clients");
        indicatorKeyMap.put("15y19y_return_men_clients", "15y19y_total_clients");
        indicatorKeyMap.put("20y24y_new_clients", "20y24y_total_clients");
        indicatorKeyMap.put("20y24y_return_clients", "20y24y_total_clients");
        indicatorKeyMap.put("20y24y_new_men_clients", "20y24y_total_clients");
        indicatorKeyMap.put("20y24y_return_men_clients", "20y24y_total_clients");
        indicatorKeyMap.put("25_new_clients", "25_total_clients");
        indicatorKeyMap.put("25_return_clients", "25_total_clients");
        indicatorKeyMap.put("25_new_men_clients", "25_total_clients");
        indicatorKeyMap.put("25_return_men_clients", "25_total_clients");
        indicatorKeyMap.put("total_new_clients", "total_total_clients");
        indicatorKeyMap.put("total_return_clients", "total_total_clients");
        indicatorKeyMap.put("total_new_men_clients", "total_total_clients");
        indicatorKeyMap.put("total_return_men_clients", "total_total_clients");
        indicatorKeyMap.put("10y14y_pop", "10y14y_total_pills");
        indicatorKeyMap.put("10y14y_coc", "10y14y_total_pills");
        indicatorKeyMap.put("10y14y_emc", "10y14y_total_pills");
        indicatorKeyMap.put("15y19y_pop", "15y19y_total_pills");
        indicatorKeyMap.put("15y19y_coc", "15y19y_total_pills");
        indicatorKeyMap.put("15y19y_emc", "15y19y_total_pills");
        indicatorKeyMap.put("20y24y_pop", "20y24y_total_pills");
        indicatorKeyMap.put("20y24y_coc", "20y24y_total_pills");
        indicatorKeyMap.put("20y24y_emc", "20y24y_total_pills");
        indicatorKeyMap.put("25_pop", "25_total_pills");
        indicatorKeyMap.put("25_coc", "25_total_pills");
        indicatorKeyMap.put("25_emc", "25_total_pills");
        indicatorKeyMap.put("total_pop", "total_total_pills");
        indicatorKeyMap.put("total_coc", "total_total_pills");
        indicatorKeyMap.put("total_emc", "total_total_pills");
        indicatorKeyMap.put("10y14y_F_mcondom", "10y14y_total_condoms");
        indicatorKeyMap.put("10y14y_F_fcondom", "10y14y_total_condoms");
        indicatorKeyMap.put("10y14y_men_F_mcondom", "10y14y_total_condoms");
        indicatorKeyMap.put("10y14y_men_F_fcondom", "10y14y_total_condoms");
        indicatorKeyMap.put("15y19y_F_mcondom", "15y19y_total_condoms");
        indicatorKeyMap.put("15y19y_F_fcondom", "15y19y_total_condoms");
        indicatorKeyMap.put("15y19y_men_F_mcondom", "15y19y_total_condoms");
        indicatorKeyMap.put("15y19y_men_F_fcondom", "15y19y_total_condoms");
        indicatorKeyMap.put("20y24y_F_mcondom", "20y24y_total_condoms");
        indicatorKeyMap.put("20y24y_F_fcondom", "20y24y_total_condoms");
        indicatorKeyMap.put("20y24y_men_F_mcondom", "20y24y_total_condoms");
        indicatorKeyMap.put("20y24y_men_F_fcondom", "20y24y_total_condoms");
        indicatorKeyMap.put("25_F_mcondom", "25_total_condoms");
        indicatorKeyMap.put("25_F_fcondom", "25_total_condoms");
        indicatorKeyMap.put("25_men_F_mcondom", "25_total_condoms");
        indicatorKeyMap.put("25_men_F_fcondom", "25_total_condoms");
        indicatorKeyMap.put("total_F_mcondom", "total_total_condoms");
        indicatorKeyMap.put("total_F_fcondom", "total_total_condoms");
        indicatorKeyMap.put("total_men_F_mcondom", "total_total_condoms");
        indicatorKeyMap.put("total_men_F_fcondom", "total_total_condoms");
        indicatorKeyMap.put("10y14y_beads", "total_beads");
        indicatorKeyMap.put("15y19y_beads", "total_beads");
        indicatorKeyMap.put("20y24y_beads", "total_beads");
        indicatorKeyMap.put("25_beads", "total_beads");
        indicatorKeyMap.put("10y14y_cousel_ANC", "total_cousel_ANC");
        indicatorKeyMap.put("15y19y_cousel_ANC", "total_cousel_ANC");
        indicatorKeyMap.put("20y24y_cousel_ANC", "total_cousel_ANC");
        indicatorKeyMap.put("25_cousel_ANC", "total_cousel_ANC");
        indicatorKeyMap.put("10y14y_cousel_delivery", "total_cousel_delivery");
        indicatorKeyMap.put("15y19y_cousel_delivery", "total_cousel_delivery");
        indicatorKeyMap.put("20y24y_cousel_delivery", "total_cousel_delivery");
        indicatorKeyMap.put("25_cousel_delivery", "total_cousel_delivery");
        indicatorKeyMap.put("10y14y_cousel_PNC", "total_cousel_PNC");
        indicatorKeyMap.put("15y19y_cousel_PNC", "total_cousel_PNC");
        indicatorKeyMap.put("20y24y_cousel_PNC", "total_cousel_PNC");
        indicatorKeyMap.put("25_cousel_PNC", "total_cousel_PNC");
        indicatorKeyMap.put("10y14y_referral", "total_fp_referral");
        indicatorKeyMap.put("15y19y_referral", "total_fp_referral");
        indicatorKeyMap.put("20y24y_referral", "total_fp_referral");
        indicatorKeyMap.put("25_referral", "total_fp_referral");
        indicatorKeyMap.put("10y14y_men_referral", "total_fp_referral");
        indicatorKeyMap.put("15y19y_men_referral", "total_fp_referral");
        indicatorKeyMap.put("20y24y_men_referral", "total_fp_referral");
        indicatorKeyMap.put("25_men_referral", "total_fp_referral");

    }

    private final JsonFormFragment formFragment;
    private final String hia2Indicator;

    public HIA2ReportFormTextWatcher(JsonFormFragment formFragment, String hi2IndicatorCode) {
        this.formFragment = formFragment;
        hia2Indicator = hi2IndicatorCode;

    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //default overridden method from interface
    }

    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        //default overridden method from interface
    }

    public void afterTextChanged(Editable editable) {

        if (indicatorKeyMap.containsKey(hia2Indicator)) {

            Integer aggregateValue = 0;

            String[] operandIndicators = aggregateFieldsMap.get(indicatorKeyMap.get(hia2Indicator));

            for (int i = 0; i < operandIndicators.length; i++) {
                MaterialEditText editTextIndicatorView = formFragment.getMainView().findViewWithTag(operandIndicators[i]);
                aggregateValue += editTextIndicatorView.getText() == null || editTextIndicatorView.getText().toString().isEmpty() ? 0 : Integer.valueOf(editTextIndicatorView.getText().toString());
            }

            MaterialEditText aggregateEditText = formFragment.getMainView().findViewWithTag(indicatorKeyMap.get(hia2Indicator));
            aggregateEditText.setText(Integer.toString(aggregateValue));
        }
    }
}
