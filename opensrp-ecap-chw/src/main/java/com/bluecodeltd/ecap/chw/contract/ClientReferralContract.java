package com.bluecodeltd.ecap.chw.contract;

import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.model.ReferralTypeModel;
import org.smartregister.util.FormUtils;

public interface ClientReferralContract {
    interface View {

        void setUpView();

        void startReferralForm(JSONObject jsonObject, ReferralTypeModel referralTypeModel);

        FormUtils getFormUtils() throws Exception;

    }
}
