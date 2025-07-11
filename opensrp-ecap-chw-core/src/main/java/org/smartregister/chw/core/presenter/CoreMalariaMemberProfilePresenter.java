package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.contract.CoreMalariaProfileContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.presenter.BaseMalariaProfilePresenter;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

public class CoreMalariaMemberProfilePresenter extends BaseMalariaProfilePresenter implements CoreMalariaProfileContract.Presenter {
    private FormUtils formUtils;
    private CoreMalariaProfileContract.Interactor interactor;

    public CoreMalariaMemberProfilePresenter(CoreMalariaProfileContract.View view, CoreMalariaProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        this.interactor = interactor;
    }

    @Override
    public CoreMalariaProfileContract.View getView() {
        if (view != null) {
            return (CoreMalariaProfileContract.View) view.get();
        }
        return null;
    }

    @Override
    public void startHfMalariaFollowupForm() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getMalariaFollowUpHfForm()));
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void createHfMalariaFollowupEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception {
        interactor.createHfMalariaFollowupEvent(allSharedPreferences, jsonString, entityID);
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }
}
