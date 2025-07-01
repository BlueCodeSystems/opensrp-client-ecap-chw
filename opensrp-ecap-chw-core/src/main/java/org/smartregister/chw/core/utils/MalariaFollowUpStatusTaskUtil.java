package org.smartregister.chw.core.utils;

import android.os.AsyncTask;
import android.view.Menu;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;
import org.smartregister.chw.malaria.dao.MalariaDao;

import java.util.Date;

public class MalariaFollowUpStatusTaskUtil extends AsyncTask<Void, Void, Void> {

    public final Menu menu;
    public final String baseEntityId;
    public MalariaFollowUpRule malariaFollowUpRule;

    public MalariaFollowUpStatusTaskUtil(Menu menu, String baseEntityId) {
        this.menu = menu;
        this.baseEntityId = baseEntityId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Date malariaTestDate = MalariaDao.getMalariaTestDate(baseEntityId);
        Date followUpDate = MalariaDao.getMalariaFollowUpVisitDate(baseEntityId);
        malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(malariaTestDate, followUpDate);
        return null;
    }

    public void setMalariaFollowUpMenuItems(String followStatus, Menu menu) {
        if (CoreConstants.VISIT_STATE.DUE.equalsIgnoreCase(followStatus) || CoreConstants.VISIT_STATE.OVERDUE.equalsIgnoreCase(followStatus)) {
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        }
    }

    @Override
    protected void onPostExecute(Void param) {
        if (malariaFollowUpRule != null) {
            setMalariaFollowUpMenuItems(malariaFollowUpRule.getButtonStatus(), menu);
        }
    }
}

