package org.smartregister.chw.anc.interactor;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.anc.util.VisitUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BaseAncMedicalHistoryInteractor implements BaseAncMedicalHistoryContract.Interactor {

    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseAncMedicalHistoryInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseAncMedicalHistoryInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void getMemberHistory(final String memberID, final Context context, final BaseAncMedicalHistoryContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {

            final List<Visit> visits = new ArrayList<>();
            try {
                visits.addAll(VisitUtils.getVisits(memberID));
            } catch (Exception e) {
                Timber.e(e);
            }
            appExecutors.mainThread().execute(() -> callBack.onDataFetched(visits));
        };

        appExecutors.diskIO().execute(runnable);
    }

}
