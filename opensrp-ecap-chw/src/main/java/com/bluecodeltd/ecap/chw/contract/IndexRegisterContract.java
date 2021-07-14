package com.bluecodeltd.ecap.chw.contract;

import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

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
        boolean saveRegistration(final List<ChildIndexEventClient> childEventClientList, final String jsonString, final boolean isEditMode, final InteractorCallBack callBack);

        void onRegistrationSaved(boolean editMode, boolean isSaved, ChildIndexEventClient childEventClient);
    }

    interface Model {
        ChildIndexEventClient processRegistration(String jsonString);
    }

    interface InteractorCallBack {

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEditMode, boolean isSaved, List<ChildIndexEventClient> familyEventClientList);

    }
}
