package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.fragment.ChooseLoginMethodFragment;
import com.bluecodeltd.ecap.chw.fragment.PinLoginFragment;
import com.bluecodeltd.ecap.chw.pinlogin.PinLogger;
import com.bluecodeltd.ecap.chw.pinlogin.PinLoginUtil;
import com.bluecodeltd.ecap.chw.presenter.LoginPresenter;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.util.Utils;

import org.smartregister.family.util.Constants;
import org.smartregister.growthmonitoring.service.intent.WeightForHeightIntentService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;


public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    public static final String TAG = BaseLoginActivity.class.getCanonicalName();
    private static final String WFH_CSV_PARSED = "WEIGHT_FOR_HEIGHT_CSV_PARSED";

    private PinLogger pinLogger = PinLoginUtil.getPinLogger();
    TextView txtUsername, txtPassword;
    boolean connected;
    private ActivityResultLauncher<String> exportDatabaseLauncher;
    private String pendingDatabaseName;
    private volatile Boolean cachedAppVersionAllowed = null;
    private volatile boolean appVersionCheckInFlight = false;
    private volatile long appVersionCheckToken = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sentry.captureMessage("testing SDK setup");
        txtUsername = findViewById(R.id.login_user_name_edit_text);
        txtPassword = findViewById(R.id.login_password_edit_text);
        exportDatabaseLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/octet-stream"),
                uri -> {
                    if (uri == null || pendingDatabaseName == null) {
                        pendingDatabaseName = null;
                        return;
                    }
                    boolean success = exportDatabaseToUri(pendingDatabaseName, uri);
                    int message = success ? R.string.export_db_done_notification : R.string.export_db_failed_notification;
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    pendingDatabaseName = null;
                });
    }

    @Override

    protected void onResume() {
        super.onResume();

        try {
            // Avoid stale values across sessions.
            cachedAppVersionAllowed = null;
            appVersionCheckInFlight = false;

            if (mLoginPresenter != null) {
                mLoginPresenter.processViewCustomizations();
            } else {
                Log.e("onResume", "LoginPresenter is null, unable to process view customizations.");
            }

            if (hasPinLogin()) {
                pinLoginAttempt();
                return;
            }

            if (mLoginPresenter != null && !mLoginPresenter.isUserLoggedOut()) {
                goToHome(false);
            } else if (mLoginPresenter == null) {
                Log.e("onResume", "LoginPresenter is null, unable to check user login status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onResume", "An unexpected error occurred: " + e.getMessage());

        }
    }

    @Override
    public boolean isAppVersionAllowed() {
        Boolean cached = cachedAppVersionAllowed;
        if (cached != null) return cached;
        return super.isAppVersionAllowed();
    }

    @Override
    public void onClick(View view) {
        if (view != null && view.getId() == R.id.login_login_btn) {
            // Avoid blocking the main thread on SQLCipher locks (SettingsRepository query).
            if (cachedAppVersionAllowed != null) {
                super.onClick(view);
                return;
            }
            if (appVersionCheckInFlight) return;
            appVersionCheckInFlight = true;
            final long token = ++appVersionCheckToken;
            view.setEnabled(false);
            Toast.makeText(this, "Checking app version…", Toast.LENGTH_SHORT).show();

            // Safety: avoid leaving the button disabled forever if the DB lock never clears.
            Threading.main(() -> Threading.mainHandler().postDelayed(() -> {
                if (!appVersionCheckInFlight) return;
                if (appVersionCheckToken != token) return;
                appVersionCheckInFlight = false;
                if (!isFinishing() && !isDestroyed()) {
                    view.setEnabled(true);
                    Toast.makeText(this, "Version check timed out. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }, 6000L));

            Threading.io(() -> {
                boolean allowed = false; // fail-closed
                try {
                    allowed = LoginActivity.super.isAppVersionAllowed();
                } catch (Throwable t) {
                    Timber.e(t, "isAppVersionAllowed check failed");
                }
                cachedAppVersionAllowed = allowed;

                Threading.main(() -> {
                    if (appVersionCheckToken != token) return;
                    appVersionCheckInFlight = false;
                    if (isFinishing() || isDestroyed()) return;
                    view.setEnabled(true);
                    LoginActivity.super.onClick(view);
                });
            });
            return;
        }

        super.onClick(view);
    }


    private void pinLoginAttempt() {
        // if the user has pin
        if (mLoginPresenter.isUserLoggedOut()) {
            if (pinLogger.isPinSet()) {
                Intent intent = new Intent(this, PinLoginActivity.class);
                intent.putExtra(PinLoginActivity.DESTINATION_FRAGMENT, PinLoginFragment.TAG);
                startActivity(intent);
                finish();
            }
        } else {
            goToHome(false);
        }
    }

    private boolean hasPinLogin() {
        return ChwApplication.getApplicationFlavor().hasPinLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (BuildConfig.DEBUG) {
            if (hasPinLogin() && !pinLogger.isFirstAuthentication()) {
                menu.add(getString(R.string.reset_pin_login));
            }
//            menu.add(getString(R.string.export_database));
            return true;
        }
        menu.clear();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.reset_pin_login))) {
            pinLogger.resetPinLogin();
            this.recreate();
            return true;
        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.export_database))) {
            String dbName = "drishti.db";
            String exportBaseName = "chw";
            String currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ENGLISH).format(new Date());
            pendingDatabaseName = dbName;
            exportDatabaseLauncher.launch(exportBaseName + "-" + currentTimeStamp + ".db");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean exportDatabaseToUri(String dbName, Uri destinationUri) {
        try (InputStream input = new FileInputStream(getDatabasePath(dbName));
             OutputStream output = getContentResolver().openOutputStream(destinationUri)) {
            if (output == null) {
                return false;
            }
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) > -1) {
                output.write(buffer, 0, length);
            }
            output.flush();
            return true;
        } catch (IOException e) {
            Timber.e(e, "exportDatabaseToUri: backup error");
            return false;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), (Object) null);
        }

        if (hasPinLogin()) {
            startPinHome(remote);
        } else {

            startHome(remote);
        }

        finish();
    }


    private void startHome(boolean remote) {
        Intent intent = new Intent(this, ChwApplication.getApplicationFlavor().launchChildClientsAtLogin() ?
                ChildRegisterActivity.class : DashboardActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        intent.putExtra("username", txtUsername.getText().toString().trim());
        intent.putExtra("password", txtPassword.getText().toString().trim());
        startActivity(intent);
    }

    private void startPinHome(boolean remote) {
        if (remote)
            pinLogger.resetPinLogin();

        if (pinLogger.isFirstAuthentication()) {
            EditText passwordEditText = findViewById(org.smartregister.R.id.login_password_edit_text);
            pinLogger.savePassword(passwordEditText.getText().toString());
        }

        if (pinLogger.isFirstAuthentication()) {
            Intent intent = new Intent(this, PinLoginActivity.class);
            intent.putExtra(PinLoginActivity.DESTINATION_FRAGMENT, ChooseLoginMethodFragment.TAG);
            startActivity(intent);
            finish();
        } else {
            // Changed something here
            Intent intent = new Intent(this, ChwApplication.getApplicationFlavor().launchChildClientsAtLogin() ?
                    ChildRegisterActivity.class : IndexRegisterActivity.class);
            intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
            startActivity(intent);
        }
    }

    private void processWeightForHeightZscoreCSV() {
        AllSharedPreferences allSharedPreferences = ChwApplication.getInstance().getContext().allSharedPreferences();
        if (ChwApplication.getApplicationFlavor().hasChildSickForm() && !allSharedPreferences.getPreference(WFH_CSV_PARSED).equals("true")) {
            WeightForHeightIntentService.startParseWFHZScores(this);
            allSharedPreferences.savePreference(WFH_CSV_PARSED, "true");
        }
    }

}
