package org.smartregister.chw.anc.presenter;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.opensrp_chw_anc.R;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class BaseAncRegisterPresenter implements BaseAncRegisterContract.Presenter, BaseAncRegisterContract.InteractorCallBack {

    protected WeakReference<BaseAncRegisterContract.View> viewReference;
    protected BaseAncRegisterContract.Interactor interactor;
    protected BaseAncRegisterContract.Model model;

    public BaseAncRegisterPresenter(BaseAncRegisterContract.View view, BaseAncRegisterContract.Model model, BaseAncRegisterContract.Interactor interactor) {
        viewReference = new WeakReference<>(view);
        this.interactor = interactor;
        this.model = model;
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            return;
        }

        BaseAncRegisterContract.View view = getView();
        if (view == null) return;

        JSONObject form = model.getFormAsJson(view.getContext(), formName, entityId, currentLocationId);
        view.startFormActivity(form);
    }

    /**
     * Override this method to provide customizations for form editing
     *
     * @param jsonString
     * @param isEditMode
     */
    @Override
    public void saveForm(String jsonString, boolean isEditMode, String table) {
        try {
            if (getView() != null)
                getView().showProgressDialog(R.string.saving_dialog_title);
            interactor.saveRegistration(jsonString, isEditMode, this, table);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onRegistrationSaved(String encounterType, boolean isEdit, boolean hasChildren) {
        if (getView() != null) {
            getView().onRegistrationSaved(encounterType, isEdit, hasChildren);
            getView().hideProgressDialog();
        }
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        if (viewIdentifiers != null)
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        if (viewIdentifiers != null)
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy
        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = Utils.getUserInitials();
        if (getView() != null) {
            getView().updateInitialsText(initials);
        }
    }

    private BaseAncRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
