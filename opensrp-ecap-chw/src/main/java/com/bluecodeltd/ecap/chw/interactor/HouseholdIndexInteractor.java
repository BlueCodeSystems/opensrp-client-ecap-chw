package com.bluecodeltd.ecap.chw.interactor;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getAllSharedPreferences;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getSyncHelper;
import static org.smartregister.chw.fp.util.FpUtil.getClientProcessorForJava;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.model.EventClient;
import com.bluecodeltd.ecap.chw.model.HouseholdIndexEventClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class HouseholdIndexInteractor implements HouseholdIndexContract.Interactor {

    private UniqueIdRepository uniqueIdRepository;
    private final HouseholdIndexContract.Presenter presenter;

    public AppExecutors appExecutors;

    public HouseholdIndexInteractor(HouseholdIndexContract.Presenter presenter) {
        this.presenter = presenter;
        this.appExecutors = new AppExecutors();
    }


    @Override
    public void getNextUniqueId(final Context context, final Triple<String, String, String> triple, final HouseholdIndexContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();


            String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";

            String xId = entityId.replaceFirst("^0+(?!$)", "");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String code = sp.getString("code", "00000");

            final String vcaId = code + "/" + xId;

            appExecutors.mainThread().execute(() -> {
                if (StringUtils.isBlank(xId)) {
                    callBack.onNoUniqueId();
                } else {
                    callBack.onUniqueIdFetched(triple, xId);
                }
            });
        };

        appExecutors.diskIO().execute(runnable);
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    @Override
    public void onDestroy(boolean var1) {

    }

    @Override
    public void saveRegistration(final List<EventClient> eventClientList, final String jsonString,
                                 final RegisterParams registerParams, final HouseholdIndexContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            saveMe(eventClientList, jsonString, registerParams);
            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved(registerParams.isEditMode()));
        };

        appExecutors.diskIO().execute(runnable);
    }

    public void saveMe(@NonNull List<EventClient> allClientEventList, @NonNull String jsonString,
                       @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < allClientEventList.size(); i++) {
                try {

                    EventClient allClientEvent = allClientEventList.get(i);
                    Client baseClient = allClientEvent.getClient();
                    Event baseEvent = allClientEvent.getEvent();
                    addClient(params, baseClient);
                    addEvent(params, currentFormSubmissionIds, baseEvent);
                    updateOpenSRPId(jsonString, params, baseClient);

                } catch (Exception e) {
                    Timber.e(e, "ChwAllClientRegisterInteractor --> saveRegistration");
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());


        } catch (Exception e) {
            Timber.e(e, "OpdRegisterInteractor --> saveRegistration");
        }
    }

    private void updateOpenSRPId(String jsonString, RegisterParams params, Client baseClient) {
        if (params.isEditMode()) {
            // UnAssign current OpenSRP ID
            if (baseClient != null) {
                String newOpenSrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey).replace("-", "");
                String currentOpenSrpId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_OPENSRP_ID).replace("-", "");
                if (!newOpenSrpId.equals(currentOpenSrpId)) {
                    //OpenSRP ID was changed
                    getUniqueIdRepository().open(currentOpenSrpId);
                }
            }

        } else {
            if (baseClient != null) {
                String openSrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                if (StringUtils.isNotBlank(openSrpId) && !openSrpId.contains(Constants.IDENTIFIER.FAMILY_SUFFIX)) {
                    //Mark OpenSRP ID as used
                    getUniqueIdRepository().close(openSrpId);
                }
            }
        }
    }

    private void addClient(@NonNull RegisterParams params, Client baseClient) throws JSONException {
        JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
        if (params.isEditMode()) {
            try {
                JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
            } catch (Exception e) {
                Timber.e(e, "ChwAllClientRegisterInteractor --> mergeAndSaveClient");
            }
        } else {
            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
        }
    }

    private void addEvent(RegisterParams params, List<String> currentFormSubmissionIds, Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(OpdJsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            currentFormSubmissionIds.add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
        }
    }
}
