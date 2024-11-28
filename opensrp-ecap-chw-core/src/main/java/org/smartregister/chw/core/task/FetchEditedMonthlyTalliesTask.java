package org.smartregister.chw.core.task;

import android.os.AsyncTask;

import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.MonthlyTally;
import org.smartregister.chw.core.repository.MonthlyTalliesRepository;

import java.util.Calendar;
import java.util.List;

public class FetchEditedMonthlyTalliesTask extends AsyncTask<Void, Void, List<MonthlyTally>> {
    private final TaskListener taskListener;

    public FetchEditedMonthlyTalliesTask(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<MonthlyTally> doInBackground(Void... params) {
        MonthlyTalliesRepository monthlyTalliesRepository = CoreChwApplication.getInstance().monthlyTalliesRepository();
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, 1); // Set date to first day of this month
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
        endDate.add(Calendar.DATE, -1); // Move the date to last day of last month

        //    return monthlyTalliesRepository.findEditedDraftMonths(null, endDate.getTime());
        return monthlyTalliesRepository.findEditedDraftMonths(null, null);

    }

    @Override
    protected void onPostExecute(List<MonthlyTally> monthlyTallies) {
        super.onPostExecute(monthlyTallies);
        taskListener.onPostExecute(monthlyTallies);
    }

    public interface TaskListener {
        void onPostExecute(List<MonthlyTally> monthlyTallies);
    }
}