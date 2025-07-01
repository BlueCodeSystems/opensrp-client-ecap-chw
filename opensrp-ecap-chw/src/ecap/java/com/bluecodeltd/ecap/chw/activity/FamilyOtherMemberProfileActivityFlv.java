package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.Utils;
import com.bluecodeltd.ecap.chw.util.UtilsFlv;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class FamilyOtherMemberProfileActivityFlv implements FamilyOtherMemberProfileActivity.Flavor {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId, final String baseEntityId) {
        return viewId -> {
            switch (viewId) {
                case org.smartregister.chw.core.R.id.call_layout:
                    Toast.makeText(activity, "Call client", Toast.LENGTH_SHORT).show();
                    FamilyCallDialogFragment.launchDialog(activity, familyBaseEntityId);
                    break;
                case org.smartregister.chw.core.R.id.refer_to_facility_layout:
                    if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
                        com.bluecodeltd.ecap.chw.util.Utils.launchClientReferralActivity(activity,  com.bluecodeltd.ecap.chw.util.Utils.getCommonReferralTypes(activity), baseEntityId);
                    } else {
                        Toast.makeText(activity, "Refer to facility", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        };
    }

    @Override
    public void updateMalariaMenuItems(String baseEntityId, Menu menu) {
        UtilsFlv.updateMalariaMenuItems(baseEntityId, menu);
    }

    @Override
    public void updateMaleFpMenuItems(String baseEntityId, Menu menu) {
        UtilsFlv.updateFpMenuItems(baseEntityId, menu);
    }

    @Override
    public void updateFpMenuItems(String baseEntityId, Menu menu) {
        UtilsFlv.updateFpMenuItems(baseEntityId, menu);
    }

    @Override
    public boolean isOfReproductiveAge(CommonPersonObjectClient commonPersonObject, String gender) {
        if (gender.equalsIgnoreCase("Female")) {
            return Utils.isMemberOfReproductiveAge(commonPersonObject, 10, 49);
        } else if (gender.equalsIgnoreCase("Male")) {
            return Utils.isMemberOfReproductiveAge(commonPersonObject, 15, 49);
        } else {
            return false;
        }
    }

    public boolean isWra(CommonPersonObjectClient commonPersonObject) {
        return Utils.isMemberOfReproductiveAge(commonPersonObject, 10, 49);

    }

    @Override
    public boolean hasANC() {
        return true;
    }
}
