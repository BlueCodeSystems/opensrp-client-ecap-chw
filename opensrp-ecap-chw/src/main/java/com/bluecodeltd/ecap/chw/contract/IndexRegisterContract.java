package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;

import org.smartregister.view.contract.BaseRegisterContract;

public interface IndexRegisterContract {

    interface View {
        void toggleDialogVisibility(boolean showDialog);
    }

    interface Presenter extends BaseRegisterContract.Presenter {
        void saveForm(String json, boolean isEditMode);

        void onRegistrationSaved();

        View getView();
    }

    interface Interactor {
        boolean saveRegistration(final ChildIndexEventClient childIndexEventClient, final boolean isEditMode);
    }

    interface Model {
        ChildIndexEventClient processRegistration(String jsonString);
    }
}
