package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;

import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;

import java.util.Collections;
import java.util.Date;

import timber.log.Timber;

public class IndexRegisterInteractor implements IndexRegisterContract.Interactor {

    private final IndexRegisterContract.Presenter presenter;

    public AppExecutors appExecutors;

    public IndexRegisterInteractor(IndexRegisterContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();

                    JSONObject newClientJsonObject = new JSONObject(JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                    if (isEditMode) {
                        JSONObject mergedClientJsonObject =
                                JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }

                    JSONObject eventJsonObject = new JSONObject(JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject, BaseRepository.TYPE_Unsynced);

                    org.smartregister.domain.Event domainEvent = JsonFormUtils.gson.fromJson(eventJsonObject.toString(),
                            org.smartregister.domain.Event.class);
                    org.smartregister.domain.Client domainClient = JsonFormUtils.gson.fromJson(eventJsonObject.toString(),
                            org.smartregister.domain.Client.class);


                    Long lastUpdatedAtDate = allSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);
                    getClientProcessorForJava().processClient(Collections.singletonList(
                            new EventClient(domainEvent, domainClient)));
                    allSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());

                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            appExecutors.mainThread().execute(presenter::onRegistrationSaved);
        };

        try {
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            return false;
        }
    }

    private ECSyncHelper getECSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }

    private AllSharedPreferences allSharedPreferences() {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }
}
