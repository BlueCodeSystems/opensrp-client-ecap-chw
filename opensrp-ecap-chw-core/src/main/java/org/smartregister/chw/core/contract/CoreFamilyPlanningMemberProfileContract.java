package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.repository.AllSharedPreferences;

public interface CoreFamilyPlanningMemberProfileContract {

    interface View {
        void startFormActivity(JSONObject formJson, Object clientDetail);
    }

    interface Presenter {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void startFamilyPlanningReferral();
    }

    interface Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }
}
