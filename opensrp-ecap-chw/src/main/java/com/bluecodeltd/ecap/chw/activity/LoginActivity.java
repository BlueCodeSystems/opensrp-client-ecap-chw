package com.bluecodeltd.ecap.chw.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

            getToken(txtUsername.getText().toString().trim(), txtPassword.getText().toString().trim());
        }

        if (hasPinLogin()) {
            startPinHome(remote);
        } else {

            startHome(remote);
        }

        finish();
    }


    private void getToken (final String username, final String password) {

        String tag_string_req = "req_login";

        String url = "https://keycloak.who.bluecodeltd.com/auth/realms/anc-stage/protocol/openid-connect/token";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {


                        String jsonInString = new Gson().toJson(response.toString().trim());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString().trim());

                            String token  = jsonObject.getString("access_token");

                            getCreds(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                error -> {

                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("grant_type","password");
                params.put("username",username);
                params.put("password",password);
                params.put("scope","openid");
                params.put("client_id",BuildConfig.OAUTH_CLIENT_ID);
                params.put("client_secret",BuildConfig.OAUTH_CLIENT_SECRET);
                return params;
            }};

        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_req);

    }


    private void getCreds(String token){

        String tag_string_creds = "req_creds";

        String url = "https://keycloak.who.bluecodeltd.com/auth/realms/anc-stage/protocol/openid-connect/userinfo";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.GET,
                url,
                (Response.Listener<String>) response -> {

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String sub = jObj.getString("sub");
                        String code = jObj.getString("code");
                        String name = jObj.getString("name");
                        String given_name = jObj.getString("given_name");
                        String family_name = jObj.getString("family_name");
                        String province = jObj.getString("province");
                        String partner = jObj.getString("partner");
                        String phone = jObj.getString("phone");
                        String district = jObj.getString("district");
                        String facility = jObj.getString("facility");
                        String email = jObj.getString("email");
                        String nrc = jObj.getString("nrc");

                        // save user data
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor edit = sp.edit();


                        edit.putString("sub", sub);
                        edit.putString("code", code);
                        edit.putString("caseworker_name", name);
                        edit.putString("given_name", given_name);
                        edit.putString("family_name", family_name);
                        edit.putString("province", province);
                        edit.putString("partner", partner);
                        edit.putString("phone", phone);
                        edit.putString("district", district);
                        edit.putString("facility", facility);
                        edit.putString("email", email);
                        edit.putString("nrc", nrc);

                        edit.commit();

                        startHome(true);


                    } catch (JSONException e){

                    }
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }};


        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_creds);

    }

    private void startHome(boolean remote) {
        Intent intent = new Intent(this, ChwApplication.getApplicationFlavor().launchChildClientsAtLogin() ?
                ChildRegisterActivity.class : IndexRegisterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
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