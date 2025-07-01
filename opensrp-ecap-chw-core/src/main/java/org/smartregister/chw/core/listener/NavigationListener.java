package org.smartregister.chw.core.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.util.Utils;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof String) {
            String tag = (String) v.getTag();
            switch (tag) {
                case CoreConstants.DrawerMenu.INDEX:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.INDEX_REGISTER_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.MOTHER_REGISTER:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.MOTHER_REGISTER_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.HOUSEHOLD_REGISTER:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.HOUSEHOLD_REGISTER_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.ALL_FAMILIES:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.BENEFICIARIES:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.BENEFICIARIES_REGISTER_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.LD:
                    Toast.makeText(activity.getApplicationContext(), CoreConstants.DrawerMenu.LD, Toast.LENGTH_SHORT).show();
                    break;
                case CoreConstants.DrawerMenu.CASE_PLANS:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.CASE_PLAN_REGISTER_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.REPORTS:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.DASHBOARD_ACTIVITY));
                    break;

                case CoreConstants.DrawerMenu.MALARIA:
                    Class malaria = getActivity(CoreConstants.REGISTERED_ACTIVITIES.MALARIA_REGISTER_ACTIVITY);
                    if (malaria == null) {
                        Toast.makeText(activity.getApplicationContext(), CoreConstants.DrawerMenu.MALARIA, Toast.LENGTH_SHORT).show();
                    } else {
                        startRegisterActivity(malaria);
                    }
                    break;
                case CoreConstants.DrawerMenu.HTS:
//                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY));
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.HTS_ACTIVITY));
                    break;
                case CoreConstants.DrawerMenu.ALL_CLIENTS:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.ALL_CLIENTS_REGISTERED_ACTIVITY));
                    break;

                case CoreConstants.DrawerMenu.PMTCT:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.PMTCT));
                    break;
                case CoreConstants.DrawerMenu.UPDATES:
                    startRegisterActivity(getActivity(CoreConstants.REGISTERED_ACTIVITIES.UPDATES_REGISTER_ACTIVITY));
                    break;
                default:
                    Utils.showShortToast(activity.getApplicationContext(), "Unspecified navigation action");
                    break;

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
