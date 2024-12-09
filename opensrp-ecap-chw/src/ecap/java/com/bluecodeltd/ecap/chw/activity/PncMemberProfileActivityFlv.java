package com.bluecodeltd.ecap.chw.activity;

import android.view.Menu;

import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.fp.dao.FpDao;
import com.bluecodeltd.ecap.chw.util.UtilsFlv;

public class PncMemberProfileActivityFlv implements PncMemberProfileActivity.Flavor {

    @Override
    public Boolean onCreateOptionsMenu(Menu menu, String baseEntityId) {
        UtilsFlv.updateMalariaMenuItems(baseEntityId, menu);
        if (FpDao.isRegisteredForFp(baseEntityId)) {
            menu.findItem(org.smartregister.chw.core.R.id.action_fp_change).setVisible(true);
        } else {
            menu.findItem(org.smartregister.chw.core.R.id.action_fp_initiation_pnc).setVisible(true);
        }
        return true;
    }
}
