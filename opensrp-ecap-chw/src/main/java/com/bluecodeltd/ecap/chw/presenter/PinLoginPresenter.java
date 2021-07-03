package com.bluecodeltd.ecap.chw.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluecodeltd.ecap.chw.contract.PinLoginContract;
import com.bluecodeltd.ecap.chw.pinlogin.PinLogger;
import com.bluecodeltd.ecap.chw.pinlogin.PinLoggerInteractor;
import com.bluecodeltd.ecap.chw.pinlogin.PinLoginUtil;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class PinLoginPresenter implements PinLoginContract.Presenter, PinLogger.EventListener {

    private PinLoginContract.Interactor interactor;
    private PinLogger logger = PinLoginUtil.getPinLogger();
    private WeakReference<PinLoginContract.View> viewWeakReference;

    public PinLoginPresenter(PinLoginContract.View view) {
        interactor = new PinLoggerInteractor();
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void localLogin(@NonNull String pin) {
        if (getView() != null)
            getView().onLoginInitiated(this);

        boolean result = logger.attemptPinVerification(pin, this);
        if (!result) {
            getView().onLoginAttemptFailed("Invalid PIN");
            return;
        }

        String userName = logger.getLoggedInUserName();
        String passWord = logger.getPassword(pin);

        interactor.authenticateUser(userName, passWord, this);
    }

    @Nullable
    @Override
    public PinLoginContract.View getView() {
        if (viewWeakReference != null)
            return viewWeakReference.get();

        return null;
    }

    @Override
    public void onError(Exception ex) {
        if (getView() != null)
            getView().onLoginAttemptFailed(ex.getMessage());
    }

    @Override
    public void onSuccess() {
        if (getView() != null)
            getView().onLoginCompleted();
    }

    @Override
    public void onEvent(String event) {
        Timber.i(event);
    }
}
