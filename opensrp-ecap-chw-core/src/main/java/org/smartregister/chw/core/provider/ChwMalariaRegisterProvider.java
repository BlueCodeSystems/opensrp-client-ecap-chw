package org.smartregister.chw.core.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.MalariaVisitUtil;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.provider.MalariaRegisterProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Date;
import java.util.Set;

public class ChwMalariaRegisterProvider extends MalariaRegisterProvider {

    private Context context;

    public ChwMalariaRegisterProvider(Context context, View.OnClickListener paginationClickListener,
                                      View.OnClickListener onClickListener, Set visibleColumns) {
        super(context, paginationClickListener, onClickListener, visibleColumns);
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        viewHolder.dueButton.setVisibility(View.GONE);
        viewHolder.dueButton.setOnClickListener(null);
        String baseEntityId = client.entityId();
        Utils.startAsyncTask(new UpdateMalariaDueButtonStatusTask(viewHolder, baseEntityId), null);
    }

    private void updateDueColumn(Button dueButton, String followStatus) {
        dueButton.setVisibility(View.VISIBLE);
        dueButton.setOnClickListener(onClickListener);
        if (CoreConstants.VISIT_STATE.OVERDUE.equalsIgnoreCase(followStatus)) {
            dueButton.setTextColor(context.getResources().getColor(R.color.white));
            dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        }
        if (CoreConstants.VISIT_STATE.DUE.equalsIgnoreCase(followStatus)) {
            dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
            dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        }
    }

    private class UpdateMalariaDueButtonStatusTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final String baseEntityId;
        private MalariaFollowUpRule malariaFollowUpRule;

        private UpdateMalariaDueButtonStatusTask(RegisterViewHolder viewHolder, String baseEntityId) {
            this.viewHolder = viewHolder;
            this.baseEntityId = baseEntityId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Date malariaTestDate = MalariaDao.getMalariaTestDate(baseEntityId);
            Date followUpDate = MalariaDao.getMalariaFollowUpVisitDate(baseEntityId);
            malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(malariaTestDate, followUpDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (malariaFollowUpRule != null && StringUtils.isNotBlank(malariaFollowUpRule.getButtonStatus()) && !CoreConstants.VISIT_STATE.EXPIRED.equalsIgnoreCase(malariaFollowUpRule.getButtonStatus())) {
                updateDueColumn(viewHolder.dueButton, malariaFollowUpRule.getButtonStatus());
            }
        }
    }
}
