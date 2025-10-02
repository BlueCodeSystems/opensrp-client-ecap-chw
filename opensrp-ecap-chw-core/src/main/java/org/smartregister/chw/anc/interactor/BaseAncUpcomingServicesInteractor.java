package org.smartregister.chw.anc.interactor;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.anc.contract.BaseAncUpcomingServicesContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.opensrp_chw_anc.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseAncUpcomingServicesInteractor implements BaseAncUpcomingServicesContract.Interactor {

    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseAncUpcomingServicesInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseAncUpcomingServicesInteractor() {
        this(new AppExecutors());
    }

    @Override
    public final void getUpComingServices(final MemberObject memberObject, final Context context, final BaseAncUpcomingServicesContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            // save it
            final List<BaseUpcomingService> services = new ArrayList<>();
            try {
                services.addAll(getMemberServices(context, memberObject));
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onDataFetched(services));
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * Compute services
     *
     * @param context
     * @param memberObject
     * @return
     */
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> services = new ArrayList<>();
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        BaseUpcomingService service1 = new BaseUpcomingService();
        service1.setServiceName(context.getString(R.string.anc_home_visit));
        service1.setServiceDate(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)));
        services.add(service1);

        List<String> names = new ArrayList<>();
        names.add("Vitamin A 10");
        names.add("Deworming");
        List<BaseUpcomingService> upcomingServices = new ArrayList<>();
        upcomingServices.add(new BaseUpcomingService("BCG"));
        upcomingServices.add(new BaseUpcomingService("Penta 1"));

        BaseUpcomingService service2 = new BaseUpcomingService();
        service2.setServiceName(names);
        service2.setUpcomingServiceList(upcomingServices);
        service2.setServiceDate(new Date(System.currentTimeMillis() + (7 * DAY_IN_MS)));
        services.add(service2);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return services;
    }
}
