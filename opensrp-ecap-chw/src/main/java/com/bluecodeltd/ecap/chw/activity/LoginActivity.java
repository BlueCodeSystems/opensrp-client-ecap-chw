package com.bluecodeltd.ecap.chw.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.fragment.ChooseLoginMethodFragment;
import com.bluecodeltd.ecap.chw.fragment.PinLoginFragment;
import com.bluecodeltd.ecap.chw.pinlogin.PinLogger;
import com.bluecodeltd.ecap.chw.pinlogin.PinLoginUtil;
import com.bluecodeltd.ecap.chw.presenter.LoginPresenter;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.google.gson.Gson;

import org.smartregister.family.util.Constants;
import org.smartregister.growthmonitoring.service.intent.WeightForHeightIntentService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;


public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    public static final String TAG = BaseLoginActivity.class.getCanonicalName();
    private static final String WFH_CSV_PARSED = "WEIGHT_FOR_HEIGHT_CSV_PARSED";

    private PinLogger pinLogger = PinLoginUtil.getPinLogger();
    TextView txtUsername, txtPassword;
    boolean connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sentry.captureMessage("testing SDK setup");
        txtUsername = findViewById(R.id.login_user_name_edit_text);
        txtPassword = findViewById(R.id.login_password_edit_text);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();

        if (hasPinLogin()) {
            pinLoginAttempt();
            return;
        }

        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
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
        if (hasPinLogin() && !pinLogger.isFirstAuthentication()) {
            menu.add(getString(R.string.reset_pin_login));
        }
        menu.add(getString(R.string.export_database));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.reset_pin_login))) {
            pinLogger.resetPinLogin();
            this.recreate();
            return true;
        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.export_database))) {
            String DBNAME = "drishti.db";
            String COPYDBNAME = "chw";

            Toast.makeText(this, R.string.export_db_notification, Toast.LENGTH_SHORT).show();
            String currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ENGLISH).format(new Date());
            if (hasPermissions()) {
                copyDatabase(DBNAME, COPYDBNAME + "-" + currentTimeStamp + ".db", this);
                Toast.makeText(this, R.string.export_db_done_notification, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean hasPermissions(){
        return PermissionUtils.isPermissionGranted(this
                , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                , CoreConstants.RQ_CODE.STORAGE_PERMISIONS);
    }

    public void copyDatabase(String dbName, String copyDbName, Context context) {
        try {
            final String inFileName = context.getDatabasePath(dbName).getPath();
            final String outFileName = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/" + copyDbName;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();

        } catch (Exception e) {
            Timber.e("copyDatabase: backup error " + e.toString());
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
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
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
                ChildRegisterActivity.class : IndexRegisterActivity.class);
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