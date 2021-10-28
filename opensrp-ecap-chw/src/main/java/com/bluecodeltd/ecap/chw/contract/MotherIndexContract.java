package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.model.EventClient;

import org.json.JSONException;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

public interface MotherIndexContract {

    interface View {
        void toggleDialogVisibility(boolean showDialog);
    }

    interface Presenter extends BaseRegisterContract.Presenter {
        void saveForm(String json, boolean isEditMode) throws JSONException;

        void onRegistrationSaved();

        MotherIndexContract.View getView();
    }

    interface Interactor {
        boolean saveRegistration(final List<EventClient> eventClients, final boolean isEditMode);
    }

    interface Model {
       List<EventClient> processRegistration(String jsonString);
    }
}
