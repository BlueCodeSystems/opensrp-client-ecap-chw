package com.bluecodeltd.ecap.chw.fragment;


import android.content.Context;

import org.smartregister.chw.core.fragment.CoreFamilyProfileChangeDialog;
import org.smartregister.chw.core.presenter.CoreFamilyChangePresenter;
import com.bluecodeltd.ecap.chw.presenter.FamilyChangePresenter;
import com.bluecodeltd.ecap.chw.util.PhoneNumberFlv;

public class FamilyProfileChangeDialog extends CoreFamilyProfileChangeDialog {

    public FamilyProfileChangeDialog() {
        phoneNumberLengthFlavor = new PhoneNumberFlv();
    }

    public static CoreFamilyProfileChangeDialog newInstance(Context context, String familyBaseEntityId, String actionType) {
        CoreFamilyProfileChangeDialog fragment = new FamilyProfileChangeDialog();
        fragment.setContext(context);
        fragment.setFamilyBaseEntityId(familyBaseEntityId);
        fragment.setActionType(actionType);
        return fragment;
    }

    @Override
    protected CoreFamilyChangePresenter getPresenter() {
        return new FamilyChangePresenter(this, this.familyID);
    }
}