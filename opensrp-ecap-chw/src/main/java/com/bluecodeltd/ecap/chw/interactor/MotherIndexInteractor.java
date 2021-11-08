package com.bluecodeltd.ecap.chw.interactor;

import android.os.Handler;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class MotherIndexInteractor implements MotherIndexContract.Interactor {

    public AppExecutors appExecutors;

    public MotherIndexInteractor(MotherIndexContract.Presenter presenter) {
        this.appExecutors = new AppExecutors();
    }

   // final Handler handler = new Handler();

    @Override
    public boolean saveRegistration(ArrayList<EventClient> eventClients, boolean isEditMode) {

        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {

                try {

                    for (int i = 0; i < eventClients.size(); i++) {

                        Log.e("xh", "EventClient At : " + eventClients.get(i));

                        Event event = eventClients.get(i).getEvent();
                        Client client = eventClients.get(i).getClient();

                        ECSyncHelper ecSyncHelper = getECSyncHelper();

                        JSONObject newClientJsonObject = new JSONObject(JsonFormUtils.gson.toJson(client));

                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);

                        JSONObject eventJsonObject = new JSONObject(JsonFormUtils.gson.toJson(event));
                        ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);

                        Long lastUpdatedAtDate = allSharedPreferences().fetchLastUpdatedAtDate(0);
                        Date currentSyncDate = new Date(lastUpdatedAtDate);

                        //Get saved event for processing
                        List<org.smartregister.domain.db.EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                        getClientProcessorForJava().processClient(savedEvents);
                        allSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());
                    }

                } catch (Exception e) {
                    Timber.e(e);
                }
                handler.postDelayed(this, 3000);


            }
        };

        handler.post(task);
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
