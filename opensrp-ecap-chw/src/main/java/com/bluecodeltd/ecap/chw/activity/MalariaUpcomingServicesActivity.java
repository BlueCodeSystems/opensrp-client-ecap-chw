package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.anc.activity.BaseAncUpcomingServicesActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncUpcomingServicesPresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.utils.MemberProfileIntentUtils;
import com.bluecodeltd.ecap.chw.interactor.MalariaUpcomingServiceInteractor;

public class MalariaUpcomingServicesActivity extends BaseAncUpcomingServicesActivity {

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, MalariaUpcomingServicesActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // See MemberProfileIntentUtils: BaseAncUpcomingServicesActivity dereferences the
        // MemberObject extra unconditionally, which NPEs if this activity is relaunched
        // without its original Intent extras (recent-tasks restore, process death).
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
        presenter = new BaseAncUpcomingServicesPresenter(memberObject, new MalariaUpcomingServiceInteractor(), this);
    }
}
