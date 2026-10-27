package org.smartregister.chw.core.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class NavigationListener implements View.OnClickListener {

    /**
     * The registered activity each drawer item opens, keyed by its tag. NavigationMenu also reads this the
     * other way round, to highlight the item for whichever screen is showing.
     */
    private static final Map<String, String> ACTIVITY_FOR_TAG = new HashMap<>();

    static {
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.INDEX, CoreConstants.REGISTERED_ACTIVITIES.INDEX_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.MOTHER_REGISTER, CoreConstants.REGISTERED_ACTIVITIES.MOTHER_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.HOUSEHOLD_REGISTER, CoreConstants.REGISTERED_ACTIVITIES.HOUSEHOLD_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.ALL_FAMILIES, CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.BENEFICIARIES, CoreConstants.REGISTERED_ACTIVITIES.BENEFICIARIES_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.CASE_PLANS, CoreConstants.REGISTERED_ACTIVITIES.CASE_PLAN_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.REPORTS, CoreConstants.REGISTERED_ACTIVITIES.DASHBOARD_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.REPORT_REGISTER, CoreConstants.REGISTERED_ACTIVITIES.REPORT_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.MALARIA, CoreConstants.REGISTERED_ACTIVITIES.MALARIA_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.HTS, CoreConstants.REGISTERED_ACTIVITIES.HTS_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.ALL_CLIENTS, CoreConstants.REGISTERED_ACTIVITIES.ALL_CLIENTS_REGISTERED_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.PMTCT, CoreConstants.REGISTERED_ACTIVITIES.PMTCT);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.APPOINTMENTS, CoreConstants.REGISTERED_ACTIVITIES.APPOINTMENTS_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.PREP, CoreConstants.REGISTERED_ACTIVITIES.PREP_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.REFERRAL_LINKAGES, CoreConstants.REGISTERED_ACTIVITIES.REFERRAL_LINKAGES_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.UPDATES, CoreConstants.REGISTERED_ACTIVITIES.UPDATES_REGISTER_ACTIVITY);
        ACTIVITY_FOR_TAG.put(CoreConstants.DrawerMenu.FLAGS, CoreConstants.REGISTERED_ACTIVITIES.FLAGS_ACTIVITY);
    }

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    /** The registered-activity key the drawer item with this tag opens, or null if it opens none. */
    public static String registeredActivityKey(String tag) {
        return tag == null ? null : ACTIVITY_FOR_TAG.get(tag);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof String) {
            String tag = (String) v.getTag();
            String key = registeredActivityKey(tag);
            if (CoreConstants.DrawerMenu.LD.equals(tag)) {
                Toast.makeText(activity.getApplicationContext(), CoreConstants.DrawerMenu.LD, Toast.LENGTH_SHORT).show();
            } else if (key == null) {
                Utils.showShortToast(activity.getApplicationContext(), "Unspecified navigation action");
            } else if (getActivity(key) == null && CoreConstants.DrawerMenu.MALARIA.equals(tag)) {
                Toast.makeText(activity.getApplicationContext(), CoreConstants.DrawerMenu.MALARIA, Toast.LENGTH_SHORT).show();
            } else {
                startRegisterActivity(getActivity(key));
            }
            navigationAdapter.setSelectedView(tag);
        }
    }

    public void startRegisterActivity(Class registerClass) {
        if (registerClass != null) {
            Intent intent = new Intent(activity, registerClass);
            intent.putExtra("username", "");
            intent.putExtra("password", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            activity.finish();
        }
    }

    private Class getActivity(String key) {
        return navigationAdapter.getRegisteredActivities().get(key);
    }
}
