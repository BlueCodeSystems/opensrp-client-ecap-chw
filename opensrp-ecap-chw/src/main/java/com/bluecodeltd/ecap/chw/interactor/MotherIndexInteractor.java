package com.bluecodeltd.ecap.chw.interactor;

import android.os.Handler;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.model.EventClient;

import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class MotherIndexInteractor implements MotherIndexContract.Interactor {

    private final MotherIndexContract.Presenter presenter;

    public AppExecutors appExecutors;

    public MotherIndexInteractor(MotherIndexContract.Presenter presenter) {
        this.presenter = presenter;
        this.appExecutors = new AppExecutors();
    }

   // final Handler handler = new Handler();

    @Override
    public boolean saveRegistration(List<EventClient> eventClients, boolean isEditMode) {


            for (EventClient eventClient : eventClients) {

                Event event = eventClient.getEvent();
                Client client = eventClient.getClient();

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
                        ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);

                        Long lastUpdatedAtDate = allSharedPreferences().fetchLastUpdatedAtDate(0);
                        Date currentSyncDate = new Date(lastUpdatedAtDate);

                        //Get saved event for processing
                        List<org.smartregister.domain.db.EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                        getClientProcessorForJava().processClient(savedEvents);
                        allSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                appExecutors.mainThread().execute(presenter::onRegistrationSaved);

            }
        return true;
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
