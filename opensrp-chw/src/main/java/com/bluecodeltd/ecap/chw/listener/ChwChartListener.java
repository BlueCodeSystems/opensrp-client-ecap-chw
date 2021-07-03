package com.bluecodeltd.ecap.chw.listener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.FragmentBaseActivity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.fragment.MyCommunityActivityDetailsFragment;
import com.bluecodeltd.ecap.chw.util.Constants;
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
