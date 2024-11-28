package com.bluecodeltd.ecap.chw.presenter;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.activity.HivTestingServiceActivity;
import com.bluecodeltd.ecap.chw.activity.PMTCTRegisterActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;
import com.bluecodeltd.ecap.chw.model.EventClient;
import com.bluecodeltd.ecap.chw.model.IndexRegisterModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.domain.FetchStatus;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.repository.UniqueIdRepository;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class PMTCTRegisterPresenter implements IndexRegisterContract.Presenter, IndexRegisterContract.InteractorCallBack {
    private String uniqueId;
    private IndexRegisterContract.View view;
    private IndexRegisterContract.Model model;
    private IndexRegisterContract.Interactor interactor;
    private UniqueIdRepository uniqueIdRepository;

    private WeakReference<IndexRegisterContract.View> activityWeakReference;

    public PMTCTRegisterPresenter(IndexRegisterContract.View view) {
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


    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            List<EventClient> eventClientList = model.processRegistration(jsonString, registerParams.getFormTag());

            if (eventClientList == null || eventClientList.isEmpty()) {
                return;
            }
            interactor.saveRegistration(eventClientList, jsonString, registerParams, this);
//            uniqueId = formJsonObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(4).optString("value");

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
    public PMTCTRegisterActivity getView() {
        if (activityWeakReference.get() != null) {
            return (PMTCTRegisterActivity) view;
        }
        return null;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }


   public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {


       if (StringUtils.isBlank(entityId)) {
           Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
           interactor.getNextUniqueId(getView().getContext(), triple, this);
           return;
       }

       JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId);
       if (getView() != null)
           getView().startFormActivity(form);

   }
}
