package org.smartregister.chw.core.presenter;

import android.app.Activity;
import android.util.Pair;

import org.smartregister.chw.core.contract.ChwNotificationDetailsContract;
import org.smartregister.chw.core.dao.ChwNotificationDao;
import org.smartregister.chw.core.domain.NotificationItem;
import org.smartregister.chw.core.interactor.BaseChwNotificationDetailsInteractor;

import java.lang.ref.WeakReference;

public class BaseChwNotificationDetailsPresenter implements ChwNotificationDetailsContract.Presenter {

    private WeakReference<ChwNotificationDetailsContract.View> view;
    private ChwNotificationDetailsContract.Interactor interactor;
    private Pair<String, String> notificationDates;

    public BaseChwNotificationDetailsPresenter(ChwNotificationDetailsContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new BaseChwNotificationDetailsInteractor(this);
    }

    @Override
    public String getClientBaseEntityId() {
        return getView().getCommonPersonObjectClient().getCaseId();
    }

    @Override
    public void getNotificationDetails(String notificationId, String notificationType) {
        interactor.fetchNotificationDetails(notificationId, notificationType);
    }

    @Override
    public ChwNotificationDetailsContract.View getView() {
        if (view != null) {
            return view.get();
        }
        return null;
    }

    @Override
    public void onNotificationDetailsFetched(NotificationItem notificationItem) {
        getView().setNotificationDetails(notificationItem);
    }

    @Override
    public void dismissNotification(String notificationId, String notificationType) {
        if (!ChwNotificationDao.isMarkedAsDone((Activity) getView(), notificationId, notificationType)) {
            getView().disableMarkAsDoneAction(true);
            interactor.createNotificationDismissalEvent(notificationId, notificationType);
        }
    }

    public void setInteractor(ChwNotificationDetailsContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setNotificationDates(Pair<String, String> notificationDates) {
        this.notificationDates = notificationDates;
    }

    @Override
    public Pair<String, String> getNotificationDates() {
        return notificationDates;
    }
}
