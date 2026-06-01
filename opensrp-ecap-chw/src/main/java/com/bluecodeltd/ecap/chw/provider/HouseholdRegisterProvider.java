package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.view_holder.HouseholdRegisterViewHolder;

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
        String updated_caregiver_name = Utils.getValue(personObjectClient.getColumnmaps(), "new_caregiver_name", true);
//        String caregiver_Name = Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_name", true);

        String is_closed = Utils.getValue(personObjectClient.getColumnmaps(), "is_closed", true);
        String baseId = Utils.getValue(personObjectClient.getColumnmaps(), "base_entity_id", true);
        String hid = Utils.getValue(personObjectClient.getColumnmaps(), "hid", true);
        String householdLookupId = firstNonBlank(householdId, hid, baseId);
        // Tag to avoid stale updates on recycled rows
        final String rowTag = firstNonBlank(householdLookupId, String.valueOf(cursor != null ? cursor.getPosition() : 0));
        householdRegisterViewHolder.itemView.setTag(R.id.tag_row_id, rowTag);
        Threading.ioBestEffort(() -> {
            List<String> genderList = new ArrayList<>();
            List<String> ageList = new ArrayList<>();
            String is_screened = null;
            try { genderList = IndexPersonDao.getGenders(householdLookupId); } catch (Exception ignored) {}
            try { ageList = IndexPersonDao.getAges(householdLookupId); } catch (Exception ignored) {}
            try { is_screened = HouseholdDao.checkIfScreened(householdLookupId); } catch (Exception ignored) {}

            String caregiverName;
            if(updated_caregiver_name.isEmpty()){
                caregiverName =  Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_name", true);
            } else {
                caregiverName =  Utils.getValue(personObjectClient.getColumnmaps(), "new_caregiver_name", true);
            }
            final String fHouseholdLookupId = householdLookupId;
            final List<String> fGenderList = genderList;
            final List<String> fAgeList = ageList;
            final String fIsScreened = is_screened;
            final String fCaregiverName = caregiverName;

            Threading.main(() -> {
                Object tag = householdRegisterViewHolder.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;
                householdRegisterViewHolder.setupViews(fCaregiverName + " " + "Household", fHouseholdLookupId, baseId, fHouseholdLookupId, fGenderList, fIsScreened, fAgeList, context);
                householdRegisterViewHolder.itemView.setOnClickListener(onClickListener);
                View columns = householdRegisterViewHolder.itemView.findViewById(R.id.register_columns);
                columns.setOnClickListener(onClickListener);
                // Click handlers expect the client on the clicked view's default tag.
                householdRegisterViewHolder.itemView.setTag(smartRegisterClient);
                columns.setTag(smartRegisterClient);
            });
        });

    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value == null) {
                continue;
            }
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        return "";
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
    public HouseholdRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View viewHolder = inflater().inflate(R.layout.household_register_item_layout, null);
        return new HouseholdRegisterViewHolder(viewHolder);
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

    }
}
