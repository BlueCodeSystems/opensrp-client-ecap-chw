package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;

import com.bluecodeltd.ecap.chw.BaseActivityTest;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.utils.CoreConstants;

public class FamilyProfileMenuActivityTest extends BaseActivityTest<FamilyProfileMenuActivity> {
    @Override
    protected Intent getControllerIntent() {
        Intent intent = new Intent();
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, "12345");
        intent.putExtra(CoreFamilyProfileMenuActivity.MENU, CoreConstants.MenuType.ChangeHead);
        return intent;
    }

    @Override
    protected Class<FamilyProfileMenuActivity> getActivityClass() {
        return FamilyProfileMenuActivity.class;
    }
}
