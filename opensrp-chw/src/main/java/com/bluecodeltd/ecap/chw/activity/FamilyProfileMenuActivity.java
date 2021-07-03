package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.bluecodeltd.ecap.chw.fragment.FamilyProfileChangeHead;
import com.bluecodeltd.ecap.chw.fragment.FamilyProfileChangePrimaryCG;

import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.Constants;

public class FamilyProfileMenuActivity extends CoreFamilyProfileMenuActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        menuOption = intent.getStringExtra(CoreFamilyProfileMenuActivity.MENU);
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        Fragment fragment;
        switch (menuOption) {
            case CoreConstants.MenuType.ChangePrimaryCare:
                fragment = FamilyProfileChangePrimaryCG.newInstance(familyBaseEntityId);
                break;
            case CoreConstants.MenuType.ChangeHead:
            default:
                fragment = FamilyProfileChangeHead.newInstance(familyBaseEntityId);
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }
}
