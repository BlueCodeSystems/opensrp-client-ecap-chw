package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.anc.activity.BaseAncUpcomingServicesActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncUpcomingServicesPresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.utils.MemberProfileIntentUtils;
import com.bluecodeltd.ecap.chw.interactor.PncUpcomingServiceInteractor;

public class PncUpcomingServicesActivity extends BaseAncUpcomingServicesActivity {

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, PncUpcomingServicesActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BaseAncUpcomingServicesActivity.onCreate()/setUpView() dereferences the MemberObject
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
        presenter = new BaseAncUpcomingServicesPresenter(memberObject, new PncUpcomingServiceInteractor(), this);
    }

}
