package org.smartregister.chw.core.contract;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.CallableInteractor;

import java.util.Date;
import java.util.Set;

public interface AncMemberProfileContract {

    interface View extends BaseAncMemberProfileContract.View {
        void setClientTasks(Set<Task> taskList);

        void startFormActivity(JSONObject formJson);

        void onVisitStatusReloaded(String status, Date lastVisitDate);

        <E extends Exception> void onError(E exception);
    }

    interface Presenter extends BaseAncMemberProfileContract.Presenter {

        CallableInteractor getCallableInteractor();

        void fetchTasks();

        void setEntityId(String entityId);

        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString) throws Exception;

        void startAncReferralForm();

        void startAncDangerSignsOutcomeForm(MemberObject memberObject);

        void createAncDangerSignsOutcomeEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;

        void refreshVisitStatus(MemberObject memberObject);
    }

    interface Interactor extends BaseAncMemberProfileContract.Interactor {
        void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;

        void getClientTasks(String planId, String baseEntityId, AncMemberProfileContract.InteractorCallBack callback);

        void createAncDangerSignsOutcomeEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception;
    }

    interface InteractorCallBack extends BaseAncMemberProfileContract.InteractorCallBack {
        void setClientTasks(Set<Task> taskList);
    }
}
