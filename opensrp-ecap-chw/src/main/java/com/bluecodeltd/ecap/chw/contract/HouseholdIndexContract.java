package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.model.HouseholdIndexEventClient;
import com.bluecodeltd.ecap.chw.model.MotherIndexEventClient;

import org.smartregister.view.contract.BaseRegisterContract;

public interface HouseholdIndexContract {

    interface View {
        void toggleDialogVisibility(boolean showDialog);
    }

    interface Presenter extends BaseRegisterContract.Presenter {
        void saveForm(String json, boolean isEditMode);

        void onRegistrationSaved();

        HouseholdIndexContract.View getView();
    }

    interface Interactor {
        boolean saveRegistration(final HouseholdIndexEventClient householdIndexEventClient, final boolean isEditMode);
    }

    interface Model {
        HouseholdIndexEventClient processRegistration(String jsonString);
    }
}
