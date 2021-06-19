package ecap.smartregister.chw.presenter;

import android.app.Activity;

import ecap.smartregister.chw.activity.UpdateRegisterDetailsActivity;
import org.smartregister.chw.core.contract.BaseChwNotificationFragmentContract;
import org.smartregister.chw.core.presenter.BaseChwNotificationFragmentPresenter;
import ecap.smartregister.chw.fragment.UpdatesRegisterFragment;
import ecap.smartregister.chw.model.UpdatesRegisterModel;
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
