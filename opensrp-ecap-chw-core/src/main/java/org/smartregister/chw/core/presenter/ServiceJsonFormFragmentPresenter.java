package org.smartregister.chw.core.presenter;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.chw.core.fragment.ServiceJsonFormFragment;

public class ServiceJsonFormFragmentPresenter extends JsonWizardFormFragmentPresenter {

    public ServiceJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    protected boolean moveToNextWizardStep() {
        if (mStepDetails != null && !mStepDetails.optString(JsonFormConstants.NEXT).equalsIgnoreCase("")) {
            JsonFormFragment next = ServiceJsonFormFragment.getFormFragment(mStepDetails.optString(JsonFormConstants.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
        }
        return false;
    }
}
