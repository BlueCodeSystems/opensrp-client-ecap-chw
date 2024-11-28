package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.chw.fp.contract.BaseFpProfileContract;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.repository.AllSharedPreferences;

public interface CoreFamilyPlanningMemberProfileContract {

    interface View extends BaseFpProfileContract.View {
        void startFormActivity(JSONObject formJson, FpMemberObject fpMemberObject);
    }

    interface Presenter extends BaseFpProfileContract.Presenter {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void startFamilyPlanningReferral();
    }

    interface Interactor extends BaseFpProfileContract.Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }
}
