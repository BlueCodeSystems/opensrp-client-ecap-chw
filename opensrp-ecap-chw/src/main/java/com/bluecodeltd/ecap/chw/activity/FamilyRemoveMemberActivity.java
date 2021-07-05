package com.bluecodeltd.ecap.chw.activity;

import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import com.bluecodeltd.ecap.chw.fragment.FamilyRemoveMemberFragment;

public class FamilyRemoveMemberActivity extends CoreFamilyRemoveMemberActivity {

    @Override
    protected void setRemoveMemberFragment() {
        this.removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
    }

}
