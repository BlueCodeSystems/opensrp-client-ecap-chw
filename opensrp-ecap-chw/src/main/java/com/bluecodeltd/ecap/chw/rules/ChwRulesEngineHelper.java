package com.bluecodeltd.ecap.chw.rules;

import com.vijay.jsonwizard.rules.RulesEngineHelper;

import com.bluecodeltd.ecap.chw.util.Utils;

public class ChwRulesEngineHelper extends RulesEngineHelper {

    public double getWFHZScore(String gender, String height, String weight) {
        return Utils.getWFHZScore(gender, height, weight);
    }
}
