package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.activity.ChildNonPmtctDetail;
import com.bluecodeltd.ecap.chw.dao.ChildFinalOutcomeDao;
import com.bluecodeltd.ecap.chw.dao.ChildLongitudinalFollowUpDao;
import com.bluecodeltd.ecap.chw.dao.ChildPostnatalCareDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.databinding.FragmentChildOverviewBinding;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildFinalOutcomeModel;
import com.bluecodeltd.ecap.chw.model.ChildLongitudinalFollowUpModel;
import com.bluecodeltd.ecap.chw.model.ChildPostnatalCareModel;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class ChildOverviewFragment extends Fragment {

    private FragmentChildOverviewBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChildOverviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ChildNonPmtctDetail activity;
        try {
            activity = (ChildNonPmtctDetail) requireActivity();
        } catch (Exception e) {
            return root;
        }

        binding.longitudinalRow.setOnClickListener(v -> activity.setViewPagerPosition(1));
        binding.postnatalRow.setOnClickListener(v -> activity.setViewPagerPosition(2));
        binding.outcomeRow.setOnClickListener(v -> activity.setViewPagerPosition(3));

        bindCaregiverAndLocation(activity.getCurrentChild());

        final String uniqueId = activity.getUniqueId();
        final String baseEntityId = activity.getBaseEntityId();
        Threading.io(() -> {
            List<ChildLongitudinalFollowUpModel> longitudinal = uniqueId != null
                    ? ChildLongitudinalFollowUpDao.listByUniqueId(uniqueId) : null;
            List<ChildPostnatalCareModel> postnatal = uniqueId != null
                    ? ChildPostnatalCareDao.listByUniqueId(uniqueId) : null;
            List<ChildFinalOutcomeModel> outcome = uniqueId != null
                    ? ChildFinalOutcomeDao.listByUniqueId(uniqueId) : null;

            String longitudinalLast = longitudinal != null && !longitudinal.isEmpty()
                    ? longitudinal.get(0).getLast_interacted_with() : null;
            String postnatalLast = postnatal != null && !postnatal.isEmpty()
                    ? postnatal.get(0).getLast_interacted_with() : null;
            String outcomeLast = outcome != null && !outcome.isEmpty()
                    ? outcome.get(0).getLast_interacted_with() : null;

            int longitudinalCount = longitudinal != null ? longitudinal.size() : 0;
            int postnatalCount = postnatal != null ? postnatal.size() : 0;
            int outcomeCount = outcome != null ? outcome.size() : 0;

            String statusCode = null;
            try {
                if (!TextUtils.isEmpty(baseEntityId)) {
                    statusCode = IndexPersonDao.getIndexStatus(baseEntityId);
                }
            } catch (Exception e) {
                Timber.e(e, "Unable to fetch case status");
            }
            final String finalStatusCode = statusCode;

            Threading.main(() -> {
                if (!isAdded() || binding == null) return;
                bindSummaryRow(binding.longitudinalSummary, longitudinalCount, longitudinalLast);
                bindSummaryRow(binding.postnatalSummary, postnatalCount, postnatalLast);
                bindSummaryRow(binding.outcomeSummary, outcomeCount, outcomeLast);
                binding.caseStatusValue.setText(caseStatusLabel(finalStatusCode));
            });
        });

        return root;
    }

    private void bindSummaryRow(TextView view, int count, String lastInteractedWith) {
        if (view == null) return;
        if (count <= 0) {
            view.setText("No records yet");
            return;
        }
        String recordWord = count == 1 ? "record" : "records";
        String lastDate = formatTimestamp(lastInteractedWith);
        if (lastDate != null) {
            view.setText(count + " " + recordWord + " • Last: " + lastDate);
        } else {
            view.setText(count + " " + recordWord);
        }
    }

    private String formatTimestamp(String rawTimestamp) {
        if (TextUtils.isEmpty(rawTimestamp)) return null;
        try {
            long timestamp = Long.parseLong(rawTimestamp);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp);
            return DateFormat.format("dd-MM-yyyy", cal).toString();
        } catch (Exception e) {
            Timber.e(e, "Unable to format timestamp");
            return null;
        }
    }

    private void bindCaregiverAndLocation(@Nullable Child child) {
        if (binding == null) return;
        if (child == null) {
            return;
        }

        binding.caregiverNameValue.setText(orNotSet(child.getCaregiver_name()));
        binding.caregiverRelationValue.setText(orNotSet(child.getRelation()));
        binding.caregiverPhoneValue.setText(orNotSet(child.getCaregiver_phone()));

        binding.facilityValue.setText(orNotSet(child.getFacility()));

        StringBuilder location = new StringBuilder();
        if (!TextUtils.isEmpty(child.getWard())) {
            location.append(child.getWard());
        }
        if (!TextUtils.isEmpty(child.getDistrict())) {
            if (location.length() > 0) location.append(" • ");
            location.append(child.getDistrict());
        }
        binding.locationValue.setText(location.length() > 0 ? location.toString() : "Not set");

        if (!TextUtils.isEmpty(child.getSchoolName())) {
            binding.schoolValue.setText(child.getSchoolName());
        } else if ("not_in_school".equals(child.getSchool())) {
            binding.schoolValue.setText("Not In School");
        } else {
            binding.schoolValue.setText("Not set");
        }

        binding.enrolledValue.setText(orNotSet(child.getDate_enrolled()));
    }

    private String caseStatusLabel(@Nullable String statusCode) {
        if ("1".equals(statusCode)) {
            return "Active";
        } else if ("0".equals(statusCode)) {
            return "Closed";
        } else if ("2".equals(statusCode)) {
            return "On Hold";
        }
        return "Not set";
    }

    private String orNotSet(String value) {
        return TextUtils.isEmpty(value) ? "Not set" : value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
