package org.smartregister.chw.core.shadows;

import android.os.Bundle;

import org.smartregister.chw.core.activity.BaseReferralTaskViewActivity;

public class ShadowReferralTaskViewActivity extends BaseReferralTaskViewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extraClientTask();
    }

    @Override
    protected void onCreation() {
        //Overridden not needed
    }

    @Override
    protected void onResumption() {
        //Overridden not needed
    }
}
