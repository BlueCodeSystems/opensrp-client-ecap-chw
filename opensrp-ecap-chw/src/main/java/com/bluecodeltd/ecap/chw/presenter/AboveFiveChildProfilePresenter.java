package com.bluecodeltd.ecap.chw.presenter;

import android.app.Activity;

import com.bluecodeltd.ecap.chw.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import com.bluecodeltd.ecap.chw.interactor.ChildProfileInteractor;
import com.bluecodeltd.ecap.chw.model.ReferralTypeModel;
import com.bluecodeltd.ecap.chw.util.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

public class AboveFiveChildProfilePresenter extends ChildProfilePresenter {

    public AboveFiveChildProfilePresenter(CoreChildProfileContract.View childView, CoreChildProfileContract.Flavor flavor, CoreChildProfileContract.Model model, String childBaseEntityId) {
        super(childView, flavor, model, childBaseEntityId);
        setView(new WeakReference<>(childView));
        setInteractor(new ChildProfileInteractor());
        getInteractor().setChildBaseEntityId(childBaseEntityId);
        setModel(model);
    }

    public void referToFacility() {
        List<ReferralTypeModel> referralTypeModels = ((AboveFiveChildProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startSickChildReferralForm();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, childBaseEntityId);
        }
    }

}
