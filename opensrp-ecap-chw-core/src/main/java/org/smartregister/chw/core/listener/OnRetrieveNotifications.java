package org.smartregister.chw.core.listener;

import android.util.Pair;

import java.util.List;

public interface OnRetrieveNotifications {
    /**
     * This is the callback method invoked when notifications lists have been retrieved
     * @param notifications the retrieved notifications
     */
    void onReceivedNotifications(List<Pair<String, String>> notifications);
}
