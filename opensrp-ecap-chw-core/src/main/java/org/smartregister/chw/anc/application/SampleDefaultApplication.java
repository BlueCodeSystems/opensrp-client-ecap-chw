package org.smartregister.chw.anc.application;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.sync.SampleSyncConfiguration;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

public class SampleDefaultApplication extends DrishtiApplication {

    public static synchronized SampleDefaultApplication getInstance() {
        return (SampleDefaultApplication) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());

        //Initialize Modules
        CoreLibrary.init(context, new SampleSyncConfiguration());
        ConfigurableViewsLibrary.init(context);
        AncLibrary.init(context, getRepository(), 1, 1);

        SyncStatusBroadcastReceiver.init(this);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());

    }

    @Override
    public void logoutCurrentUser() {
        Timber.v("logoutCurrentUser");
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

}
