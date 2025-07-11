package com.bluecodeltd.ecap.chw.util;

import android.app.Activity;
import android.util.Pair;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.UpdateRegisterDetailsActivity;
import org.smartregister.chw.core.adapter.NotificationListAdapter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.List;

import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;

public class NotificationsUtil {

    public static void handleNotificationRowClick(Activity launcherActivity, View view,
                                                  NotificationListAdapter notificationListAdapter, String baseEntityId) {
        if (view.getTag() instanceof NotificationListAdapter.NotificationRowViewHolder
                && notificationListAdapter.canOpen) {
            notificationListAdapter.canOpen = false;
            NotificationListAdapter.NotificationRowViewHolder notificationRowViewHolder =
                    (NotificationListAdapter.NotificationRowViewHolder) view.getTag();
            Pair<String, String> notificationRecord = notificationListAdapter.getNotificationRecords()
                    .get(notificationRowViewHolder.getAdapterPosition());
            CommonPersonObjectClient client = getClientDetails(baseEntityId);
            UpdateRegisterDetailsActivity.startActivity(client, launcherActivity,
                    notificationRecord.first, notificationRecord.second);
        }
    }


    @NotNull
    private static CommonPersonObjectClient getClientDetails(String baseEntityId) {
        final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName)
                .findByBaseEntityId(baseEntityId);
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(personObject.getCaseId(),
                personObject.getDetails(), "");
        commonPersonObjectClient.setColumnmaps(personObject.getColumnmaps());
        commonPersonObjectClient.setDetails(personObject.getDetails());
        return commonPersonObjectClient;
    }

    public static void handleReceivedNotifications(Activity activity, List<Pair<String, String>> notifications,
                                                   NotificationListAdapter notificationListAdapter) {
        notificationListAdapter.getNotificationRecords().clear();
        notificationListAdapter.getNotificationRecords().addAll(notifications);
        notificationListAdapter.notifyDataSetChanged();
        activity.findViewById(org.smartregister.chw.core.R.id.notification_and_referral_row).setVisibility(View.VISIBLE);
        activity.findViewById(org.smartregister.chw.core.R.id.notification_and_referral_row).setVisibility(View.VISIBLE);
    }
}
