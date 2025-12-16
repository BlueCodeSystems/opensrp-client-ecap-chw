package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.IndexMotherDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.IndexMotherModel;
import com.bluecodeltd.ecap.chw.view_holder.MotherRegisterViewHolder;
import com.bluecodeltd.ecap.chw.util.Threading;

import org.smartregister.chw.core.holders.FooterViewHolder;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MotherRegisterProvider implements RecyclerViewProvider<MotherRegisterViewHolder>, View.OnClickListener {

    private final Context context;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationViewHandler;


    public MotherRegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationViewHandler) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.paginationViewHandler = paginationViewHandler;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, MotherRegisterViewHolder motherRegisterViewHolder) {
        CommonPersonObjectClient personObjectClient = (CommonPersonObjectClient) smartRegisterClient;
        String fullName = Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_name", true);
        String household_id = Utils.getValue(personObjectClient.getColumnmaps(), "household_id", true);
        String caregiverBirthDate = Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_birth_date", true);
        String lastInteractedWith = Utils.getValue(personObjectClient.getColumnmaps(), "last_interacted_with", true);

        final String age = getMotherAge(caregiverBirthDate);
        final String enrollmentLabel = buildEnrollmentLabel(lastInteractedWith);

        // Tag to avoid stale updates on recycled rows.
        final String rowTag = household_id != null ? household_id : "";
        motherRegisterViewHolder.itemView.setTag(R.id.tag_row_id, rowTag);

        // Set fallbacks immediately; refine asynchronously.
        motherRegisterViewHolder.setupViews(fullName, household_id, age, buildChildrenSummary("0", null), enrollmentLabel);

        final String fHouseholdId = household_id;
        Threading.ioBestEffort(() -> {
            String childrenCount = null;
            IndexMotherModel indexMother = null;
            try { childrenCount = IndexPersonDao.countChildren(fHouseholdId); } catch (Exception ignored) { }
            try { indexMother = IndexMotherDao.getIndexMotherByHouseholdId(fHouseholdId); } catch (Exception ignored) { }
            final String ageBand = indexMother != null ? indexMother.getMother_children_age_band() : null;
            final String childrenSummary = buildChildrenSummary(childrenCount, ageBand);
            Threading.main(() -> {
                Object tag = motherRegisterViewHolder.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;
                motherRegisterViewHolder.setupViews(fullName, fHouseholdId, age, childrenSummary, enrollmentLabel);
            });
        });
        motherRegisterViewHolder.itemView.setOnClickListener(onClickListener);
        motherRegisterViewHolder.itemView.setTag(smartRegisterClient);
    }

    private String getMotherAge(String birthDateRaw) {
        if (birthDateRaw == null || birthDateRaw.trim().isEmpty()) {
            return "";
        }
        // Try common formats; fall back gracefully on failure
        String[] patterns = new String[]{
                "dd-MM-yyyy",
                "dd-MM-uuuu",
                "dd MMM yyyy"
        };
        Date birthDate = null;
        for (String pattern : patterns) {
            try {
                birthDate = new SimpleDateFormat(pattern, Locale.ENGLISH).parse(birthDateRaw);
                if (birthDate != null) break;
            } catch (ParseException ignored) { }
        }
        if (birthDate == null) {
            return "";
        }
        long diffMillis = System.currentTimeMillis() - birthDate.getTime();
        if (diffMillis <= 0) {
            return "";
        }
        long years = diffMillis / (365L * 24 * 60 * 60 * 1000);
        return years > 0 ? years + " yrs" : "";
    }

    private String buildChildrenSummary(String childrenCount, String ageBand) {
        String count = (childrenCount == null || childrenCount.trim().isEmpty()) ? "0" : childrenCount.trim();
        if ("1".equals(count)) {
            return "1 child";
        }
        return count + " children";
    }

    private String buildEnrollmentLabel(String lastInteractedWithRaw) {
        if (lastInteractedWithRaw == null || lastInteractedWithRaw.trim().isEmpty()) {
            return "";
        }
        // last_interacted_with is stored as a long timestamp (event.version)
        try {
            long timestamp = Long.parseLong(lastInteractedWithRaw);
            Date date = new Date(timestamp);
            String formatted = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(date);
            return "Enrolled: " + formatted;
        } catch (Exception e) {
            return "";
        }
    }


    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNextPage, boolean hasPreviousPage) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount, totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNextPage ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPreviousPage ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationViewHandler);
        footerViewHolder.previousPageView.setOnClickListener(paginationViewHandler);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MotherRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View viewHolder = inflater().inflate(R.layout.mother_layout, null);
        return new MotherRegisterViewHolder(viewHolder);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater().inflate(org.smartregister.R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }


    @Override
    public void onClick(View v) {

        Utils.showShortToast(v.getContext(), ((TextView)v).getText().toString() +" Clicked");

        // Toast.makeText(v.getContext(), ((TextView)v).getText().toString(), Toast.LENGTH_LONG).show();
    }

}
