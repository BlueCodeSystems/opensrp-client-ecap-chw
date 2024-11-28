package org.smartregister.chw.core.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.chw.core.interactor.ServiceJsonFormInteractor;
import org.smartregister.chw.core.presenter.ServiceJsonFormFragmentPresenter;
import org.smartregister.chw.core.utils.CoreConstants;

public class ServiceJsonFormFragment extends JsonWizardFormFragment {

    public static ServiceJsonFormFragment getFormFragment(String stepName) {
        ServiceJsonFormFragment jsonFormFragment = new ServiceJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CoreConstants.KeyIndicatorsUtil.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new ServiceJsonFormFragmentPresenter(this, ServiceJsonFormInteractor.getServiceInteractorInstance());
    }
}
