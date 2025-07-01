package org.smartregister.chw.core.presenter;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.core.contract.CorePncMemberProfileContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class CorePncMemberProfilePresenter extends BaseAncMemberProfilePresenter implements CorePncMemberProfileContract.Presenter {
    private WeakReference<CorePncMemberProfileContract.View> view;
    private CorePncMemberProfileContract.Interactor interactor;
    private FormUtils formUtils;

    public CorePncMemberProfilePresenter(CorePncMemberProfileContract.View view, CorePncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        this.view = new WeakReference<>(view);
        this.interactor = interactor;
    }

    @Override
    public CorePncMemberProfileContract.View getView() {
        if (view != null) {
            return view.get();
        }
        return null;
    }


    @Override
    public void startPncDangerSignsOutcomeForm() {
        try {
            getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getPncDangerSignsOutcomeForm()));
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void createPncDangerSignsOutcomeEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception {
        interactor.createPncDangerSignsOutcomeEvent(allSharedPreferences, jsonString, entityID);
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
