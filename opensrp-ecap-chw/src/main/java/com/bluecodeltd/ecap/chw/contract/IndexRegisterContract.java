package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;

import org.smartregister.view.contract.BaseRegisterContract;

public interface IndexRegisterContract {

    interface View {
        void toggleDialogVisibility(boolean showDialog);
    }

    interface Presenter extends BaseRegisterContract.Presenter {
        void saveForm(String json);

        void onRegistrationSaved();
    }

    interface Interactor {
        void saveRegistration(ChildIndexEventClient childIndexEventClient);
    }
}
