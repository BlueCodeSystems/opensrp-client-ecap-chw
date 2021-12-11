package com.bluecodeltd.ecap.chw.contract;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluecodeltd.ecap.chw.model.EventClient;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.pojo.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

public interface IndexRegisterContract {

    interface View {
        void toggleDialogVisibility(boolean showDialog);
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveForm(String jsonString, @NonNull RegisterParams registerParams);

        View getView();


    }

    interface Interactor {

        void getNextUniqueId(Context context, Triple<String, String, String> triple, IndexRegisterContract.InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

        void saveRegistration(List<EventClient> opdEventClientList, String jsonString, RegisterParams registerParams, IndexRegisterContract.InteractorCallBack callBack);

    }

    interface Model {

        List<EventClient> processRegistration(String jsonString, FormTag formTag);

        @Nullable
        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws JSONException;


    }

    interface InteractorCallBack {
        void onNoUniqueId();

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onRegistrationSaved(boolean isEdit);

    }
}
