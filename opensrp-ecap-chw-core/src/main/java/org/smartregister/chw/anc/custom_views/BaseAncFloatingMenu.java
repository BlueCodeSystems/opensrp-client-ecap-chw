package org.smartregister.chw.anc.custom_views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.anc.fragment.BaseAncWomanCallDialogFragment;
import org.smartregister.chw.opensrp_chw_anc.R;

public class BaseAncFloatingMenu extends LinearLayout implements View.OnClickListener {
    private String phoneNumber, familyHeadName, familyHeadPhone, womanName, womanProfileType;

    public BaseAncFloatingMenu(Context context, String ancWomanName, String ancWomanPhone, String ancFamilyHeadName, String ancFamilyHeadPhone, String profileType) {
        super(context);
        initUi();
        womanName = ancWomanName;
        phoneNumber = ancWomanPhone;
        familyHeadName = ancFamilyHeadName;
        familyHeadPhone = ancFamilyHeadPhone;
        womanProfileType = profileType;
    }

    protected void initUi() {
        inflate(getContext(), R.layout.view_anc_call_woma_floating_menu, this);
        findViewById(R.id.anc_fab).setOnClickListener(this);
    }

    public BaseAncFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public BaseAncFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.anc_fab) {
            Activity activity = (Activity) getContext();
            BaseAncWomanCallDialogFragment.launchDialog(activity, womanName, phoneNumber, familyHeadName, familyHeadPhone, womanProfileType);
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFamilyHeadName() {
        return familyHeadName;
    }

    public String getFamilyHeadPhone() {
        return familyHeadPhone;
    }

    public String getWomanName() {
        return womanName;
    }

    public String getWomanProfileType() {
        return womanProfileType;
    }
}
