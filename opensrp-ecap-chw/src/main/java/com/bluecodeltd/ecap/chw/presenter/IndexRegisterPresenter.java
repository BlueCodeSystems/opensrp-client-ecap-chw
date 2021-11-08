package com.bluecodeltd.ecap.chw.presenter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluecodeltd.ecap.chw.activity.IndexRegisterActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;
import com.bluecodeltd.ecap.chw.model.EventClient;
import com.bluecodeltd.ecap.chw.model.IndexRegisterModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.domain.FetchStatus;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class IndexRegisterPresenter implements IndexRegisterContract.Presenter, IndexRegisterContract.InteractorCallBack {

    private IndexRegisterContract.View view;
    private IndexRegisterContract.Model model;
    private IndexRegisterContract.Interactor interactor;

    private WeakReference<IndexRegisterContract.View> activityWeakReference;

    public IndexRegisterPresenter(IndexRegisterContract.View view) {
        this.view = view;
        activityWeakReference = new WeakReference<>(view);

        interactor = new IndexRegisterInteractor(this);
        setModel(new IndexRegisterModel());
    }



    public void setModel(IndexRegisterContract.Model model) {
        this.model = model;
    }

    @Override
    public void registerViewConfigurations(List<String> list) {
        //Overridden
    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {
        //Overridden
    }

    @Override
    public void onDestroy(boolean b) {
        //Overridden
    }

    @Override
    public void updateInitials() {
        //Overridden
    }

   /* @Override
    public void saveForm(String jsonString, boolean isEditMode) {

        try {

            view.toggleDialogVisibility(true);


            ChildIndexEventClient childIndexEventClient = model.processRegistration(jsonString);

            if (childIndexEventClient == null) {
                return;
            }

            interactor.saveRegistration(childIndexEventClient, isEditMode);

        } catch (Exception e) {
            Timber.e(e);
        }
    }*/

    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        try {
            List<EventClient> eventClientList = model.processRegistration(jsonString, registerParams.getFormTag());

            if (eventClientList == null || eventClientList.isEmpty()) {
                return;
            }
            interactor.saveRegistration(eventClientList, jsonString, registerParams, this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null)
            getView().displayShortToast(org.smartregister.family.R.string.no_unique_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        if (getView() != null) {
            try {
                startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
            } catch (Exception e) {
                Timber.e(e);
                getView().displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
            }
        }
    }

    @Override
    public void onRegistrationSaved(boolean inEditMode) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
            NavigationMenu navigationMenu = NavigationMenu.getInstance((Activity) activityWeakReference.get(),
                    null, null);
            if (navigationMenu != null) {
                navigationMenu.refreshCount();
            }
        }
    }
    @Override
    public IndexRegisterActivity getView() {
        if (activityWeakReference.get() != null) {
            return (IndexRegisterActivity) view;
        }
        return null;
    }


   public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

       if (StringUtils.isBlank(entityId)) {
           Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
           interactor.getNextUniqueId(triple, this);
           return;
       }

       JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId);
       if (getView() != null)
           getView().startFormActivity(form);

   }
}
