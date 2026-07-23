package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

public class ReportJsonFormFragment extends JsonWizardFormFragment {

    public static ReportJsonFormFragment getFormFragment(String stepName) {
        ReportJsonFormFragment jsonFormFragment = new ReportJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonWizardFormFragmentPresenter createPresenter() {
        return new JsonWizardFormFragmentPresenter(this, new JsonFormInteractor());
    }
}
