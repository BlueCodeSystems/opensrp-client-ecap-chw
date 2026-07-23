package com.bluecodeltd.ecap.chw.repository;

import android.content.Context;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.chw.core.repository.CoreChwRepository;
import org.smartregister.repository.DrishtiRepository;


import timber.log.Timber;

public class ChwRepository extends CoreChwRepository {
    private static final String BINDTYPES_ASSET = "bindtypes.json";

    private Context context;

    public ChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), ChwApplication.getApplicationFlavor().chwAppInstance().getCommonFtsObject(), safeSharedRepositoriesArray(openSRPContext));
        this.context = context;
    }

    private static DrishtiRepository[] safeSharedRepositoriesArray(org.smartregister.Context openSRPContext) {
        try {
            return openSRPContext.sharedRepositoriesArray();
        } catch (Throwable t) {
            Timber.w(t, "Skipping shared repositories initialization because bindtypes could not be loaded");
            return new DrishtiRepository[0];
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(CoreChwRepository.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        ChwRepositoryFlv.onUpgrade(context, db, oldVersion, newVersion);
    }
}
