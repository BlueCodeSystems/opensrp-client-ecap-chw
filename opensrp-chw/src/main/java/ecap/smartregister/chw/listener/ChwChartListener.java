package ecap.smartregister.chw.listener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import ecap.smartregister.chw.R;
import ecap.smartregister.chw.activity.FragmentBaseActivity;
import ecap.smartregister.chw.application.ChwApplication;
import ecap.smartregister.chw.fragment.MyCommunityActivityDetailsFragment;
import ecap.smartregister.chw.util.Constants;
import org.smartregister.reporting.domain.PieChartSlice;
import org.smartregister.reporting.listener.PieChartSelectListener;

public class ChwChartListener implements PieChartSelectListener {
    private Activity context;

    public ChwChartListener(Activity context) {
        this.context = context;
    }

    @Override
    public void handleOnSelectEvent(PieChartSlice pieChartSlice) {
        if (ChwApplication.getApplicationFlavor().showMyCommunityActivityReport()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ReportParameters.INDICATOR_CODE, pieChartSlice.getKey());
            FragmentBaseActivity.startMe(context, MyCommunityActivityDetailsFragment.TAG, context.getString(R.string.children), bundle);

        } else {
            Toast.makeText(context, pieChartSlice.getLabel(), Toast.LENGTH_SHORT).show();
        }
    }
}
