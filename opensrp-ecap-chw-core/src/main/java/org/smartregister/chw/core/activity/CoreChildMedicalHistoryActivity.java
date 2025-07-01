package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.anc.activity.BaseAncMedicalHistoryActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.contract.CoreChildMedicalHistoryContract;
import org.smartregister.chw.core.interactor.CoreChildMedicalHistoryActivityInteractor;
import org.smartregister.chw.core.presenter.CoreChildMedicalHistoryPresenter;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

public abstract class CoreChildMedicalHistoryActivity extends BaseAncMedicalHistoryActivity implements CoreChildMedicalHistoryContract.View {

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, CoreChildMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    protected abstract Flavor getFlavor();

    @Override
    public void initializePresenter() {
        presenter = new CoreChildMedicalHistoryPresenter(new CoreChildMedicalHistoryActivityInteractor(), this, memberObject.getBaseEntityId());
    }

    @Override
    public void onDataReceived(List<Visit> visits, Map<String, List<Vaccine>> vaccines, List<ServiceRecord> serviceRecords) {
        View view = renderView(visits, vaccines, serviceRecords);
        linearLayout.addView(view, 0);
    }

    @Override
    public View renderView(List<Visit> visits, Map<String, List<Vaccine>> vaccines, List<ServiceRecord> serviceRecords) {
        Flavor flavor = getFlavor();
        View view = flavor.bindViews(this);

        displayLoadingState(true);
        flavor.processViewData(visits, vaccines, serviceRecords, this);
        displayLoadingState(false);

        return view;
    }

    public interface Flavor {
        View bindViews(Activity activity);

        void processViewData(
                List<Visit> visits,
                Map<String, List<Vaccine>> vaccines,
                List<ServiceRecord> serviceRecords,
                Context context
        );

        CoreChildUtils.Flavor getChildUtils();
    }

}
