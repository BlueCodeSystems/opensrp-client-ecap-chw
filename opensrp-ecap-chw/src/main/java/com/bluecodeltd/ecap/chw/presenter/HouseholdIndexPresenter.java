package com.bluecodeltd.ecap.chw.presenter;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.activity.HouseholdIndexActivity;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.interactor.HouseholdIndexInteractor;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;
import com.bluecodeltd.ecap.chw.model.EventClient;
import com.bluecodeltd.ecap.chw.model.HouseholdIndexEventClient;
import com.bluecodeltd.ecap.chw.model.HouseholdIndexModel;
import com.bluecodeltd.ecap.chw.model.IndexRegisterModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.domain.FetchStatus;
import org.smartregister.opd.pojo.RegisterParams;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class HouseholdIndexPresenter implements HouseholdIndexContract.Presenter, HouseholdIndexContract.InteractorCallBack{

    private HouseholdIndexContract.View view;
    private HouseholdIndexContract.Model model;
    private HouseholdIndexContract.Interactor interactor;

    private WeakReference<HouseholdIndexContract.View> activityWeakReference;

    public HouseholdIndexPresenter(HouseholdIndexContract.View view) {
        this.view = view;
        activityWeakReference = new WeakReference<>(view);

        interactor = new HouseholdIndexInteractor(this);
        setModel(new HouseholdIndexModel());
    }

    public void setModel(HouseholdIndexContract.Model model) {
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
    public HouseholdIndexActivity getView() {
        if (activityWeakReference.get() != null) {
            return (HouseholdIndexActivity) view;
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
    public void onRegistrationSaved(boolean isEdit) {
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
}
