package org.smartregister.chw.core.application;

import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.sync.SampleSyncConfiguration;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.family.BuildConfig;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

/**
 * @author rkodev
 */
public class TestApplication extends CoreChwApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context, new SampleSyncConfiguration());

        ConfigurableViewsLibrary.init(context);

        SyncStatusBroadcastReceiver.init(this);
        FamilyLibrary.init(context, getMetadata(), BuildConfig.VERSION_CODE, 2);
        PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, 2);

        setTheme(org.smartregister.family.R.style.FamilyTheme_NoActionBar);
    }

    @Override
    public FamilyMetadata getMetadata() {
        return Mockito.mock(FamilyMetadata.class);
    }

    @Override
    public ArrayList<String> getAllowedLocationLevels() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> getFacilityHierarchy() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultLocationLevel() {
        return "default";
    }

    @Override
    public Repository getRepository() {
        repository = mock(Repository.class);
        return repository;
    }

    @Override
    public void onTerminate() {
        Robolectric.flushBackgroundThreadScheduler();
    }

}
