package org.smartregister.chw.core.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

public interface BaseChwNotificationFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {
        void displayDetailsActivity(CommonPersonObjectClient client, String notificationId, String notificationType);
    }

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter();
    }
}
