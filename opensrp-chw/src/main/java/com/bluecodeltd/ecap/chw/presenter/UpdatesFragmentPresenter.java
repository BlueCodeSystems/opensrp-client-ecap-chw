package com.bluecodeltd.ecap.chw.presenter;

import android.app.Activity;

import com.bluecodeltd.ecap.chw.activity.UpdateRegisterDetailsActivity;
import org.smartregister.chw.core.contract.BaseChwNotificationFragmentContract;
import org.smartregister.chw.core.presenter.BaseChwNotificationFragmentPresenter;
import com.bluecodeltd.ecap.chw.fragment.UpdatesRegisterFragment;
import com.bluecodeltd.ecap.chw.model.UpdatesRegisterModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class UpdatesFragmentPresenter extends BaseChwNotificationFragmentPresenter {

    public UpdatesFragmentPresenter(BaseChwNotificationFragmentContract.View view) {
        super(view, new UpdatesRegisterModel());
    }

    @Override
    public void displayDetailsActivity(CommonPersonObjectClient commonPersonObjectClient, String notificationId, String notificationType) {
        Activity activity = ((UpdatesRegisterFragment) getView()).getActivity();
        UpdateRegisterDetailsActivity.startActivity(commonPersonObjectClient, activity, notificationId, notificationType);
    }
}
