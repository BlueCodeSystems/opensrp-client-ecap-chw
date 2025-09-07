package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.fp.features.profile.contract.ProfileContract;
import org.smartregister.fp.common.domain.ClientDetail;
import org.smartregister.repository.AllSharedPreferences;

public interface CoreFamilyPlanningMemberProfileContract {

    interface View extends ProfileContract.View {
        void startFormActivity(JSONObject formJson, ClientDetail clientDetail);
    }

    interface Presenter extends ProfileContract.Presenter {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void startFamilyPlanningReferral();
    }

    interface Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }
}
