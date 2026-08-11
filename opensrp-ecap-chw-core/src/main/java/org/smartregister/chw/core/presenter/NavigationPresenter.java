package org.smartregister.chw.core.presenter;

import android.app.Activity;

import androidx.preference.PreferenceManager;

import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.chw.core.interactor.NavigationInteractor;
import org.smartregister.chw.core.job.CoreBasePncCloseJob;
import org.smartregister.chw.core.job.HomeVisitServiceJob;
import org.smartregister.chw.core.job.VaccineRecurringServiceJob;
import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.BuildConfig;
import org.smartregister.chw.core.util.DirectusFlagsRepository;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncTaskServiceJob;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationPresenter implements NavigationContract.Presenter {

    private NavigationContract.Model mModel;
    private NavigationContract.Interactor mInteractor;
    private WeakReference<NavigationContract.View> mView;
    private HashMap<String, String> tableMap = new HashMap<>();

    public NavigationPresenter(CoreApplication application, NavigationContract.View view, NavigationModel.Flavor modelFlavor) {
        mView = new WeakReference<>(view);

        mInteractor = NavigationInteractor.getInstance();
        mInteractor.setApplication(application);

        mModel = NavigationModel.getInstance();
        mModel.setNavigationFlavor(modelFlavor);

        initialize();
    }

    private void initialize() {
        tableMap.put(CoreConstants.DrawerMenu.INDEX, CoreConstants.TABLE_NAME.EC_CLIENT_INDEX);
        tableMap.put(CoreConstants.DrawerMenu.MOTHER_REGISTER, CoreConstants.TABLE_NAME.EC_MOTHER_INDEX);
        tableMap.put(CoreConstants.DrawerMenu.ALL_FAMILIES, CoreConstants.TABLE_NAME.FAMILY);
        tableMap.put(CoreConstants.DrawerMenu.HOUSEHOLD_REGISTER, CoreConstants.TABLE_NAME.EC_HOUSEHOLD);
        tableMap.put(CoreConstants.DrawerMenu.CHILD_CLIENTS, CoreConstants.TABLE_NAME.CHILD);
        tableMap.put(CoreConstants.DrawerMenu.ANC_CLIENTS, CoreConstants.TABLE_NAME.ANC_MEMBER);
        tableMap.put(CoreConstants.DrawerMenu.ANC, CoreConstants.TABLE_NAME.ANC_MEMBER);
        tableMap.put(CoreConstants.DrawerMenu.PNC, CoreConstants.TABLE_NAME.ANC_PREGNANCY_OUTCOME);
        tableMap.put(CoreConstants.DrawerMenu.REFERRALS, CoreConstants.TABLE_NAME.EC_REFERRAL);
        tableMap.put(CoreConstants.DrawerMenu.MALARIA, CoreConstants.TABLE_NAME.MALARIA_CONFIRMATION);
        tableMap.put(CoreConstants.DrawerMenu.FAMILY_PLANNING, FamilyPlanningConstants.DBConstants.FAMILY_PLANNING_TABLE);
        tableMap.put(CoreConstants.DrawerMenu.ALL_CLIENTS, CoreConstants.TABLE_NAME.FAMILY_MEMBER);
        tableMap.put(CoreConstants.DrawerMenu.UPDATES, CoreConstants.TABLE_NAME.NOTIFICATION_UPDATE);
        tableMap.put(CoreConstants.DrawerMenu.HTS, CoreConstants.TABLE_NAME.EC_HIV_TESTING_SERVICE);
        tableMap.put(CoreConstants.DrawerMenu.PMTCT, CoreConstants.TABLE_NAME.EC_MOTHER_PMTCT);
        tableMap.put(CoreConstants.DrawerMenu.REPORT_REGISTER, "report_register_total");
    }

    public HashMap<String, String> getTableMap() {
        return tableMap;
    }

    public void setTableMap(HashMap<String, String> tableMap) {
        this.tableMap = tableMap;
    }

    public void updateTableMap(HashMap<String, String> mp) {
        for (Map.Entry<String, String> stringEntry : mp.entrySet()) {
            tableMap.put(stringEntry.getKey(), stringEntry.getValue());
        }
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return mView.get();
    }


    @Override
    public void refreshNavigationCount(final Activity activity) {

        int x = 0;
        while (x < mModel.getNavigationItems().size()) {
            final int finalX = x;
            String menuTitle = mModel.getNavigationItems().get(x).getMenuTitle();
            if (CoreConstants.DrawerMenu.FLAGS.equals(menuTitle)) {
                android.content.SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                DirectusFlagsRepository.fetchCount(
                        flagAssignmentValue(prefs),
                        prefs.getString("facility", ""),
                        prefs.getString("phone", ""),
                        BuildConfig.DIRECTUS_EMAIL,
                        BuildConfig.DIRECTUS_PASSWORD,
                        BuildConfig.DIRECTUS_FLAGS_CASEWORKER_FIELD,
                        BuildConfig.DIRECTUS_FLAGS_COLLECTION,
                        new DirectusFlagsRepository.CountCallback() {
                            @Override
                            public void onResult(int count) {
                                mModel.getNavigationItems().get(finalX).setRegisterCount(count);
                                getNavigationView().refreshCount();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                            }
                        }
                );
                x++;
                continue;
            }
            mInteractor.getRegisterCount(tableMap.get(menuTitle), new NavigationContract.InteractorCallback<Integer>() {
                @Override
                public void onResult(Integer result) {

                    if(mModel.getNavigationItems().get(finalX).getMenuTitle().equals("Stock Management"))
                    {
                        mModel.getNavigationItems().get(finalX);
                    }

                    mModel.getNavigationItems().get(finalX).setRegisterCount(result);
                    getNavigationView().refreshCount();
                }

                @Override
                public void onError(Exception e) {
                    // getNavigationView().displayToast(activity, "Error retrieving count for " + tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));
                    //Timber.e("Error retrieving count for %s", tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));
                }
            });
            x++;
        }

    }



    private String flagAssignmentValue(android.content.SharedPreferences prefs) {
        String field = BuildConfig.DIRECTUS_FLAGS_CASEWORKER_FIELD;
        if ("caseworker_phone".equals(field)) {
            return prefs.getString("phone", "");
        }
        if ("caseworker_name".equals(field)) {
            return prefs.getString("caseworker_name", "");
        }
        try {
            String registeredAnm = org.smartregister.Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            if (registeredAnm != null && !registeredAnm.trim().isEmpty()) {
                return registeredAnm.trim();
            }
        } catch (Exception ignored) {
        }
        String[] keys = new String[]{"provider_id", "providerId", "provider", "sub"};
        for (String key : keys) {
            String value = prefs.getString(key, "");
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }
    @Override
    public void refreshLastSync() {
        // get last sync date
        getNavigationView().refreshLastSync(mInteractor.sync());
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(mModel.getCurrentUser());
    }

    @Override
    public void sync(Activity activity) {
        CoreBasePncCloseJob.scheduleJobImmediately(CoreBasePncCloseJob.TAG);
        HomeVisitServiceJob.scheduleJobImmediately(HomeVisitServiceJob.TAG);
        VaccineRecurringServiceJob.scheduleJobImmediately(VaccineRecurringServiceJob.TAG);
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
        //PlanIntentServiceJob.scheduleJobImmediately(PlanIntentServiceJob.TAG);
        SyncTaskServiceJob.scheduleJobImmediately(SyncTaskServiceJob.TAG);
    }

    @Override
    public List<NavigationOption> getOptions() {
        return mModel.getNavigationItems();
    }


}
