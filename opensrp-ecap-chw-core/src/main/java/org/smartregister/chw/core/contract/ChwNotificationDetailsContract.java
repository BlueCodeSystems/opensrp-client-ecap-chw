package org.smartregister.chw.core.contract;

import android.util.Pair;

import org.smartregister.chw.core.domain.NotificationItem;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public interface ChwNotificationDetailsContract {

    interface View {
        void setNotificationDetails(NotificationItem notificationItem);

        void initPresenter();

        void disableMarkAsDoneAction(boolean disable);

        void goToMemberProfile();

        void setCommonPersonsObjectClient(CommonPersonObjectClient client);

        CommonPersonObjectClient getCommonPersonObjectClient();
    }

    interface Presenter {

        String getClientBaseEntityId();

        void getNotificationDetails(String notificationId, String notificationType);

        View getView();

        void onNotificationDetailsFetched(NotificationItem notificationItem);

        void dismissNotification(String notificationId, String notificationType);

        void setNotificationDates(Pair<String, String> dates);

        Pair<String, String> getNotificationDates();
    }

    interface Interactor {
        /**
         * Fetch the notification details
         *
         * @param notificationId   unique notification id
         * @param notificationType type of notification
         */
        void fetchNotificationDetails(String notificationId, String notificationType);

        /**
         * Crete a Notification dismissal entry for the provided notification id
         *
         * @param notificationId Notification id
         */
        void createNotificationDismissalEvent(String notificationId, String notificationType);
    }
}
