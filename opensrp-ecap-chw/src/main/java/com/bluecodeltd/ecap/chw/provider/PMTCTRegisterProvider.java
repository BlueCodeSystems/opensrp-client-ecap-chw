package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.view_holder.PMTCTRegisterViewHolder;

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

public class PMTCTRegisterProvider implements RecyclerViewProvider<PMTCTRegisterViewHolder> {

    private final Context context;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationViewHandler;
    String age;


    public PMTCTRegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationViewHandler) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.paginationViewHandler = paginationViewHandler;
    }

    private String getAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            if(periodBetweenDateOfBirthAndNow.getYears() == 1){

                return periodBetweenDateOfBirthAndNow.getYears() +" Year Old";

            } else {
                return periodBetweenDateOfBirthAndNow.getYears() +" Years Old";
            }

        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){

            if (periodBetweenDateOfBirthAndNow.getMonths() == 1){

                return periodBetweenDateOfBirthAndNow.getMonths() +" Month Old";

            } else {
                return periodBetweenDateOfBirthAndNow.getMonths() +" Months Old";
            }

        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return periodBetweenDateOfBirthAndNow.getDays() +" Days Old";
        }
        else return "Age Not Set";
    }


    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, PMTCTRegisterViewHolder pmtctRegisterViewHolder) {
        CommonPersonObjectClient personObjectClient = (CommonPersonObjectClient) smartRegisterClient;

        String BaseEntityId = Utils.getValue(personObjectClient.getColumnmaps(), "base_entity_id", false);
        String firstName = Utils.getValue(personObjectClient.getColumnmaps(), "first_name", true);
        String lastName = Utils.getValue(personObjectClient.getColumnmaps(), "last_name", true);
        String clientId = Utils.getValue(personObjectClient.getColumnmaps(), "pmtct_id", false);
//        String gender = Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_sex", true);
        String gender = "";
        String household_id = Utils.getValue(personObjectClient.getColumnmaps(), "mothers_smh_no", true);
        String birthdate = Utils.getValue(personObjectClient.getColumnmaps(), "mothers_age", true);
//        String client_type = Utils.getValue(personObjectClient.getColumnmaps(), "testing_modality", true);
        String client_type = "";

        if(birthdate != null && !birthdate.isEmpty())
        {
            age = getAge(birthdate);
        }

        pmtctRegisterViewHolder.setupViews(firstName+" "+lastName,"ID : " + clientId, gender, age,client_type);
        pmtctRegisterViewHolder.itemView.setOnClickListener(onClickListener);
        pmtctRegisterViewHolder.itemView.findViewById(R.id.index_warning).setOnClickListener(onClickListener);
        pmtctRegisterViewHolder.itemView.setTag(smartRegisterClient);
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder,int currentPageCount, int totalPageCount, boolean hasNextPage, boolean hasPreviousPage) {
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
    public PMTCTRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View viewHolder = inflater().inflate(R.layout.ptcmt_register_item_layout, null);
        return new PMTCTRegisterViewHolder(viewHolder);
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
