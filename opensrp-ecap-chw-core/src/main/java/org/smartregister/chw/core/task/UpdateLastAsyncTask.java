package org.smartregister.chw.core.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.domain.VisitSummary;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.getDuration;

public class UpdateLastAsyncTask extends AsyncTask<Void, Void, Void> {
    public final Context context;
    private final CommonRepository commonRepository;
    public final RegisterViewHolder viewHolder;
    public final String baseEntityId;
    private final Rules rules;
    public CommonPersonObject commonPersonObject;
    public ChildVisit childVisit;
    public View.OnClickListener onClickListener;
    private SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public UpdateLastAsyncTask(Context context, CommonRepository commonRepository, RegisterViewHolder viewHolder, String baseEntityId, View.OnClickListener onClickListener) {
        this.context = context;
        this.commonRepository = commonRepository;
        this.viewHolder = viewHolder;
        this.baseEntityId = baseEntityId;
        this.onClickListener = onClickListener;
        this.rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.HOME_VISIT);
    }

    @Override
    public Void doInBackground(Void... params) {
        if (commonRepository != null) {
            commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);

            Map<String, VisitSummary> map = VisitDao.getVisitSummary(baseEntityId);
            if (map != null) {
                VisitSummary notDoneSummary = map.get(CoreConstants.EventType.CHILD_VISIT_NOT_DONE);
                VisitSummary lastVisitSummary = map.get(CoreConstants.EventType.CHILD_HOME_VISIT);

                long lastVisit = 0;
                long visitNot = 0;
                long dateCreated = 0;
                try {
                    String createVal = Utils.getValue(commonPersonObject.getColumnmaps(), ChwDBConstants.DATE_CREATED, false);
                    if (StringUtils.isNotBlank(createVal))
                        dateCreated = ISO8601DATEFORMAT.parse(createVal).getTime();

                } catch (Exception e) {
                    Timber.e(e);
                }
                if (lastVisitSummary != null)
                    lastVisit = lastVisitSummary.getVisitDate().getTime();

                if (notDoneSummary != null)
                    visitNot = notDoneSummary.getVisitDate().getTime();

                String dobString = getDuration(Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false));
                childVisit = CoreChildUtils.getChildVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);
            }
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        if (commonPersonObject != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.DUE.name())) {
                setVisitButtonDueStatus(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.OVERDUE.name())) {
                setVisitButtonOverdueStatus(context, viewHolder.dueButton, childVisit.getNoOfMonthDue());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.LESS_TWENTY_FOUR.name())) {
                setVisitLessTwentyFourView(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.VISIT_THIS_MONTH.name())) {
                setVisitAboveTwentyFourView(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                setVisitNotDone(context, viewHolder.dueButton);
            }
        } else {
            viewHolder.dueButton.setVisibility(View.GONE);
        }

    }

    public void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.record_home_visit));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    public void setVisitButtonOverdueStatus(Context context, Button dueButton, String lastVisitDays) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.R.color.white));
        if (TextUtils.isEmpty(lastVisitDays)) {
            dueButton.setText(context.getString(R.string.record_visit));
        } else {
            dueButton.setText(context.getString(R.string.due_visit, lastVisitDays));
        }

        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    public void setVisitLessTwentyFourView(Context context, Button dueButton) {
        setVisitAboveTwentyFourView(context, dueButton);
    }

    public void setVisitAboveTwentyFourView(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.R.color.alert_complete_green));
        dueButton.setText(context.getString(R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    public void setVisitNotDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.family.R.color.progress_orange));
        dueButton.setText(context.getString(R.string.visit_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.transparent));
        dueButton.setOnClickListener(null);
    }
}
