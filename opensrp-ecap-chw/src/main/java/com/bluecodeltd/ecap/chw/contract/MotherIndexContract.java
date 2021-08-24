package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.MotherIndexModel;

import org.smartregister.view.contract.BaseRegisterContract;

public interface MotherIndexContract {

    interface View {
        void toggleDialogVisibility(boolean showDialog);
    }

    interface Presenter extends BaseRegisterContract.Presenter {
        void saveForm(String json, boolean isEditMode);

        void onRegistrationSaved();

        MotherIndexContract.View getView();
    }

    interface Interactor {
        boolean saveRegistration(final MotherIndexModel motherIndexModel, final boolean isEditMode);
    }

    interface Model {
        MotherIndexModel processRegistration(String jsonString);
    }
}
