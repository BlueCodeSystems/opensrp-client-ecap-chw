package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.chw.malaria.contract.MalariaProfileContract;
import org.smartregister.repository.AllSharedPreferences;

public interface CoreMalariaProfileContract {
    interface View extends MalariaProfileContract.View {
        void startFormActivity(JSONObject formJson);
    }

    interface Presenter extends MalariaProfileContract.Presenter {
        void startHfMalariaFollowupForm();

        void createHfMalariaFollowupEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }

    interface Interactor extends MalariaProfileContract.Interactor {
        void createHfMalariaFollowupEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityIDd) throws Exception;
    }
}
