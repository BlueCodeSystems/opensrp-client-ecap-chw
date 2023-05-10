package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.view_holder.HouseholdRegisterViewHolder;
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
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HouseholdRegisterProvider implements RecyclerViewProvider<HouseholdRegisterViewHolder>, View.OnClickListener{

    private final Context context;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationViewHandler;


    public HouseholdRegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationViewHandler) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.paginationViewHandler = paginationViewHandler;
    }



    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, HouseholdRegisterViewHolder householdRegisterViewHolder) {
        CommonPersonObjectClient personObjectClient = (CommonPersonObjectClient) smartRegisterClient;

        String householdId = Utils.getValue(personObjectClient.getColumnmaps(), "household_id", false);
        String caregiver_Name = Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_name", true);
        String is_closed = Utils.getValue(personObjectClient.getColumnmaps(), "is_closed", true);
        String baseId = Utils.getValue(personObjectClient.getColumnmaps(), "base_entity_id", true);
        List<String> genderList = IndexPersonDao.getGenders(householdId);
        List<String> ageList = IndexPersonDao.getAges(householdId);
        String is_screened = HouseholdDao.checkIfScreened(householdId);
        //String is_closed = HouseholdDao.getHouseholdByBaseId(baseId).getStatus();


        householdRegisterViewHolder.setupViews(caregiver_Name + " " + "Household", householdId, baseId, householdId, genderList, is_screened, ageList, context);
        householdRegisterViewHolder.itemView.setOnClickListener(onClickListener);
        householdRegisterViewHolder.itemView.findViewById(R.id.register_columns).setOnClickListener(onClickListener);
        householdRegisterViewHolder.itemView.setTag(smartRegisterClient);

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
    public HouseholdRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View viewHolder = inflater().inflate(R.layout.household_register_item_layout, null);
        return new HouseholdRegisterViewHolder(viewHolder);
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

    }
}
