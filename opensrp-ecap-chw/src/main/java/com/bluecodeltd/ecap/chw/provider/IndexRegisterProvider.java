package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.FamilyDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.view_holder.IndexRegisterViewHolder;

import org.smartregister.chw.core.holders.FooterViewHolder;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;

public class IndexRegisterProvider implements RecyclerViewProvider<IndexRegisterViewHolder>, View.OnClickListener {

    private final Context context;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationViewHandler;

    public IndexRegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationViewHandler) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.paginationViewHandler = paginationViewHandler;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, IndexRegisterViewHolder indexRegisterViewHolder) {
        CommonPersonObjectClient personObjectClient = (CommonPersonObjectClient) smartRegisterClient;

        String BaseEntityId = Utils.getValue(personObjectClient.getColumnmaps(), "base_entity_id", false);
        String firstName = Utils.getValue(personObjectClient.getColumnmaps(), "first_name", true);
        String lastName = Utils.getValue(personObjectClient.getColumnmaps(), "last_name", true);
        String childId = Utils.getValue(personObjectClient.getColumnmaps(), "unique_id", false);

        int plans = CasePlanDao.checkCasePlan(childId);

        String is_index = IndexPersonDao.checkIndexPerson(BaseEntityId);

        String status = IndexPersonDao.getIndexStatus(BaseEntityId);

        indexRegisterViewHolder.setupViews(firstName +" "+lastName, "ID : " + childId, plans, is_index, status);
        indexRegisterViewHolder.itemView.setOnClickListener(onClickListener);
        indexRegisterViewHolder.itemView.setTag(smartRegisterClient);


        indexRegisterViewHolder.caseplan_layout.setOnClickListener(v -> {

            if (v.getId() == R.id.due_button) {

                Intent intent = new Intent(context, IndexDetailsActivity.class);
                intent.putExtra("Child",  BaseEntityId);
                intent.putExtra("clients", personObjectClient);
                intent.putExtra("tab", 1);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder,int currentPageCount, int totalPageCount, boolean hasNextPage, boolean hasPreviousPage) {
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
    public IndexRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View viewHolder = inflater().inflate(R.layout.index_register_item_layout, null);
        return new IndexRegisterViewHolder(viewHolder);
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
    public void onClick(View view) {

    }
}
