package com.bluecodeltd.ecap.chw.task;

import android.app.Activity;
import android.os.Bundle;

import com.bluecodeltd.ecap.chw.activity.AboveFiveChildProfileActivity;
import com.bluecodeltd.ecap.chw.activity.AncMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.ChildProfileActivity;
import com.bluecodeltd.ecap.chw.activity.FamilyPlanningMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.MalariaProfileActivity;
import com.bluecodeltd.ecap.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.task.CoreChwNotificationGoToMemberProfileTask;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ChwGoToMemberProfileBasedOnRegisterTask extends CoreChwNotificationGoToMemberProfileTask {

    public ChwGoToMemberProfileBasedOnRegisterTask(CommonPersonObjectClient commonPersonObjectClient, Bundle bundle, String notificationType, Activity activity) {
        super(commonPersonObjectClient, bundle, notificationType, activity);
    }

    @Override
    protected void goToFpProfile(String baseEntityId, Activity activity) {
        FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(activity, FpDao.getMember(baseEntityId));
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return ChildProfileActivity.class;
    }

    @Override
    protected Class<? extends BaseAncMemberProfileActivity> getAncMemberProfileActivityClass() {
        return AncMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends BasePncMemberProfileActivity> getPncMemberProfileActivityClass() {
        return PncMemberProfileActivity.class;
    }

    @Override
    protected Class<? extends BaseMalariaProfileActivity> getMalariaProfileActivityClass() {
        return MalariaProfileActivity.class;
    }
}
