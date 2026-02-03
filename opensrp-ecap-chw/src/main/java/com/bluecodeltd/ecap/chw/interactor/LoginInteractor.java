package com.bluecodeltd.ecap.chw.interactor;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.LoginJobScheduler;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.security.SecurityHelper;
import org.smartregister.util.NetworkUtils;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/***
 * @author rkodev
 */
public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    /**
     * add all schedule jobs to the schedule instance to enable
     * job start at pin login
     */
    private LoginJobScheduler scheduler = new LoginJobSchedulerProvider();

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    public void login(WeakReference<BaseLoginContract.View> view, String userName, char[] password) {
        BaseLoginContract.View loginView = view != null ? view.get() : getLoginView();
        if (loginView == null) {
            SecurityHelper.clearArray(password);
            return;
        }

        loginView.hideKeyboard();
        loginView.enableLoginButton(false);
        loginView.showProgress(true);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            boolean hasNetwork = NetworkUtils.isNetworkAvailable();
            boolean refreshTokenExpired = isRefreshTokenExpired(userName);
            boolean isLocalLogin;

            if (!hasNetwork) {
                if (refreshTokenExpired) {
                    loginView.getAppCompatActivity().runOnUiThread(() -> {
                        loginView.showProgress(false);
                        loginView.enableLoginButton(true);
                        loginView.showErrorDialog(getApplicationContext().getString(R.string.offline_login_token_expired));
                    });
                    SecurityHelper.clearArray(password);
                    return;
                }
                attemptOfflineTokenLogin(loginView, userName);
                SecurityHelper.clearArray(password);
                return;
            } else {
                isLocalLogin = !getSharedPreferences().fetchForceRemoteLogin(userName);
                org.smartregister.Context opensrpContext = CoreLibrary.getInstance().context();
                if (refreshTokenExpired || (opensrpContext.getAppProperties().getPropertyBoolean(AllConstants.PROPERTY.ALLOW_OFFLINE_LOGIN_WITH_INVALID_TOKEN)
                        && isLocalLogin
                        && HttpURLConnection.HTTP_UNAUTHORIZED == getSharedPreferences().getLastAuthenticationHttpStatus())) {
                    isLocalLogin = false;
                }
            }

            boolean canLocalLogin = isLocalLogin && getSharedPreferences().isRegisteredANM(userName);
            loginView.getAppCompatActivity().runOnUiThread(() -> loginWithLocalFlag(view, canLocalLogin, userName, password));
        });
    }

    private void attemptOfflineTokenLogin(BaseLoginContract.View loginView, String userName) {
        boolean loginSuccessful = getUserService().localLoginWith(userName);

        loginView.getAppCompatActivity().runOnUiThread(() -> {
            loginView.showProgress(false);
            loginView.enableLoginButton(true);

            if (loginSuccessful) {
                loginView.goToHome(false);
                CoreLibrary.getInstance().initP2pLibrary(userName);
                new Thread(() -> {
                    scheduleJobsImmediately();
                    scheduleJobsPeriodically();
                    CoreLibrary.getInstance().context().getUniqueIdRepository().releaseReservedIds();
                }).start();
                return;
            }

            loginView.showErrorDialog(getApplicationContext().getString(org.smartregister.R.string.unauthorized));
        });
    }

    @Override
    protected void scheduleJobsPeriodically() {
        scheduler.scheduleJobsPeriodically();
    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
        scheduler.scheduleJobsImmediately();
    }
}
