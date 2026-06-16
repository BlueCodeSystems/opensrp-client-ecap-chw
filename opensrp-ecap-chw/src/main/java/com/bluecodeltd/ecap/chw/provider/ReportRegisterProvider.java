package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.view_holder.ReportRegisterViewHolder;

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

public class ReportRegisterProvider implements RecyclerViewProvider<ReportRegisterViewHolder> {

    private final Context context;
    private final View.OnClickListener onClickListener;
    private final View.OnClickListener paginationViewHandler;

    public ReportRegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationViewHandler) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.paginationViewHandler = paginationViewHandler;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, ReportRegisterViewHolder viewHolder) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) smartRegisterClient;

        String services = Utils.getValue(client.getColumnmaps(), "services", true);
        String householdId = Utils.getValue(client.getColumnmaps(), "household_id", true);
        String date = Utils.getValue(client.getColumnmaps(), "date", true);

        String title = getTitle(services);
        String householdLabel = context.getString(R.string.report_household_prefix, safe(householdId));
        String summary = buildSummary(client);
        String dateLabel = context.getString(R.string.report_date_prefix, safe(date));

        viewHolder.setupViews(title, householdLabel, summary, dateLabel);
        viewHolder.itemView.setOnClickListener(onClickListener);
        viewHolder.itemView.setTag(smartRegisterClient);
    }

    private String getTitle(String services) {
        if ("caregiver".equalsIgnoreCase(services)) {
            return context.getString(R.string.report_register_caregiver_title);
        }
        if ("household".equalsIgnoreCase(services)) {
            return context.getString(R.string.report_register_household_title);
        }
        return context.getString(R.string.report_register_title);
    }

    private String buildSummary(CommonPersonObjectClient client) {
        String summary = firstNonBlank(
                Utils.getValue(client.getColumnmaps(), "services_household", true),
                Utils.getValue(client.getColumnmaps(), "services_caregiver", true),
                Utils.getValue(client.getColumnmaps(), "health_services", true),
                Utils.getValue(client.getColumnmaps(), "hh_level_services", true),
                Utils.getValue(client.getColumnmaps(), "other_services_household", true),
                Utils.getValue(client.getColumnmaps(), "other_services_caregiver", true),
                Utils.getValue(client.getColumnmaps(), "other_health_services", true)
        );

        if (summary == null || summary.trim().isEmpty()) {
            return context.getString(R.string.report_summary_empty);
        }

        return summary.replace("_", " ");
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
        // No-op
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
    public ReportRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View view = inflater().inflate(R.layout.report_register_item, null);
        return new ReportRegisterViewHolder(view);
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
}
