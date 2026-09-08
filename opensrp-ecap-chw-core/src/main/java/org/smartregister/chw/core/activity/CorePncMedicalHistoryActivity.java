package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.smartregister.chw.anc.domain.GroupedVisit;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.utils.MemberProfileIntentUtils;
import org.smartregister.chw.pnc.activity.BasePncMedicalHistoryActivity;
import org.smartregister.chw.pnc.contract.BasePncMedicalHistoryContract;
import org.smartregister.chw.pnc.interactor.BasePncMedicalHistoryPresenter;

import java.util.List;

public abstract class CorePncMedicalHistoryActivity extends BasePncMedicalHistoryActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BasePncMedicalHistoryActivity ultimately delegates to BaseAncMedicalHistoryActivity,
        // which dereferences the MemberObject extra unconditionally in onCreate()/setUpView().
        // This activity can be relaunched by Android without its original Intent extras
        // (recent-tasks restore, process death), so seed a placeholder before delegating.
        boolean missingMemberObject = !MemberProfileIntentUtils.hasMemberObjectExtra(this);
        if (missingMemberObject) {
            MemberProfileIntentUtils.ensureMemberObjectExtra(this);
        }
        super.onCreate(savedInstanceState);
        if (missingMemberObject) {
            finish();
        }
    }

    @Override
    public void initializePresenter() {
        pncPresenter = new BasePncMedicalHistoryPresenter(getPncMedicalHistoryInteractor(), this, memberObject.getBaseEntityId());
    }

    protected abstract BasePncMedicalHistoryContract.Interactor getPncMedicalHistoryInteractor();

    public interface Flavor {

        View bindViews(Activity activity);

        void processViewData(List<GroupedVisit> groupedVisits, Context context, MemberObject memberObject);
    }

}
