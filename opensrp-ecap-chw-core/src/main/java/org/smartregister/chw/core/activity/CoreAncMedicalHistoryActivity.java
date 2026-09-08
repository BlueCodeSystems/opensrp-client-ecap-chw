package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.smartregister.chw.anc.activity.BaseAncMedicalHistoryActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.CoreBaseAncMedicalHistoryInteractor;
import org.smartregister.chw.core.utils.MemberProfileIntentUtils;

import java.util.List;

public abstract class CoreAncMedicalHistoryActivity extends BaseAncMedicalHistoryActivity {

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, CoreAncMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BaseAncMedicalHistoryActivity.onCreate()/setUpView() dereferences the MemberObject
        // extra unconditionally. This activity can be relaunched by Android without its
        // original Intent extras (recent-tasks restore, process death), so seed a placeholder
        // before delegating -- see MemberProfileIntentUtils.
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
        presenter = new BaseAncMedicalHistoryPresenter(new CoreBaseAncMedicalHistoryInteractor(), this, memberObject.getBaseEntityId());
    }

    public interface Flavor {
        View bindViews(Activity activity);

        void processViewData(List<Visit> visits, Context context);
    }
}
