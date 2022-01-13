package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.view_holder.IndexRegisterViewHolder;
import com.bluecodeltd.ecap.chw.view_holder.MotherRegisterViewHolder;

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

        motherRegisterViewHolder.setupViews(fullName, household_id);
        motherRegisterViewHolder.itemView.setOnClickListener(onClickListener);
        motherRegisterViewHolder.itemView.setTag(smartRegisterClient);
    }


    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNextPage, boolean hasPreviousPage) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.str_page_info), currentPageCount, totalPageCount));

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
        View view = inflater().inflate(org.smartregister.chw.core.R.layout.smart_register_pagination, parent, false);
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
