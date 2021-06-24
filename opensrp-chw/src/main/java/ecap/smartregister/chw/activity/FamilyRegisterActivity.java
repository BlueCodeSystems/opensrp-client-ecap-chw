package ecap.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vijay.jsonwizard.domain.Form;

import ecap.smartregister.chw.R;
import ecap.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.custom_views.FamilyFloatingMenu;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import ecap.smartregister.chw.fragment.FamilyRegisterFragment;
import ecap.smartregister.chw.listener.ChwBottomNavigationListener;
import ecap.smartregister.chw.util.Constants;
import ecap.smartregister.chw.util.Utils;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

import timber.log.Timber;

public class FamilyRegisterActivity extends CoreFamilyRegisterActivity {


    public static void startFamilyRegisterForm(Activity activity) {
        Intent intent = new Intent(activity, FamilyRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, CoreConstants.ACTION.START_REGISTRATION);
        activity.startActivity(intent);

    }

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        ChwApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)
        FamilyFloatingMenu familyFloatingMenu = new FamilyFloatingMenu(this
        );
       // familyFloatingMenu.setGravity();
        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startFormActivity("initial_level_facility_form","","");

        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        Timber.v("startFormActivity");
    }
}