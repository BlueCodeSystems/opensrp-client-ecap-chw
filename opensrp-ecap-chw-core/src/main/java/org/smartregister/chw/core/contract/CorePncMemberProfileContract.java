package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.repository.AllSharedPreferences;

public interface CorePncMemberProfileContract {

    interface View extends BaseAncMemberProfileContract.View {
        void startFormActivity(JSONObject formJson);
    }

    interface Presenter extends BaseAncMemberProfileContract.Presenter {
        void startPncDangerSignsOutcomeForm();

        void createPncDangerSignsOutcomeEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }

    interface Interactor extends BaseAncMemberProfileContract.Interactor {
        void createPncDangerSignsOutcomeEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }
}
