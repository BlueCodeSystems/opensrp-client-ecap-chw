package org.smartregister.chw.core.interactor;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.CoreCommunityRespondersContract;
import org.smartregister.chw.core.model.CommunityResponderModel;
import org.smartregister.chw.core.repository.CommunityResponderRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;

public class CoreCommunityRespondersInteractor implements CoreCommunityRespondersContract.Interactor {

    protected AppExecutors appExecutors;

    public CoreCommunityRespondersInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    CoreCommunityRespondersInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void addCommunityResponder(String jsonString, CoreCommunityRespondersContract.InteractorCallBack callBack) {
        createAddCommunityResponderEvent(jsonString);
        FormUtils.processEvent();
        fetchAllCommunityResponders(callBack);

    }

    @Override
    public void fetchAllCommunityResponders(CoreCommunityRespondersContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            List<CommunityResponderModel> communityResponders = getAllCommunityResponders();
            appExecutors.mainThread().execute(() -> callBack.updateCommunityRespondersList(communityResponders));
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void removeCommunityResponder(String baseEntityId, CoreCommunityRespondersContract.InteractorCallBack callBack) {
        try {
            createRemoveCommunityResponderEvent(baseEntityId);
            FormUtils.processEvent();
        } catch (Exception e) {
            Timber.e(e, "Interactor --> confirmPurgeResponder --> removeCommunityResponder");
        }
        fetchAllCommunityResponders(callBack);
    }

    public List<CommunityResponderModel> getAllCommunityResponders() {
        CommunityResponderRepository communityResponderRepository = CoreChwApplication.getInstance().communityResponderRepository();
        return communityResponderRepository.readAllResponders();
    }

    private void createAddCommunityResponderEvent(String jsonString) {
        try {
            Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(Utils.context().allSharedPreferences(), jsonString, CoreConstants.TABLE_NAME.COMMUNITY_RESPONDERS);
            if (StringUtils.isBlank(baseEvent.getBaseEntityId()))
                baseEvent.setBaseEntityId(UUID.randomUUID().toString());
            CoreJsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), baseEvent);
            JSONObject eventJson = new JSONObject(CoreJsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRemoveCommunityResponderEvent(String baseEntityID) {
        JSONObject form = FormUtils.getFormUtils().getFormJson(CoreConstants.JSON_FORM.COMMUNITY_RESPONDER_REGISTRATION_FORM);
        AllSharedPreferences allSharedPreferences = Utils.context().allSharedPreferences();
        Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, form.toString(), CoreConstants.TABLE_NAME.COMMUNITY_RESPONDERS);
        baseEvent.setFormSubmissionId(UUID.randomUUID().toString());
        baseEvent.setBaseEntityId(baseEntityID);
        baseEvent.setEventType(CoreConstants.EventType.REMOVE_COMMUNITY_RESPONDER);
        CoreJsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);
        try {
            JSONObject eventJson = new JSONObject(CoreJsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEntityID, eventJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
