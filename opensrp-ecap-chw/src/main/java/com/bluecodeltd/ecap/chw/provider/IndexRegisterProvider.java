package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.view_holder.IndexRegisterViewHolder;

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
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class IndexRegisterProvider implements RecyclerViewProvider<IndexRegisterViewHolder> {

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
        String gender = Utils.getValue(personObjectClient.getColumnmaps(), "gender", true);
        String household_id = Utils.getValue(personObjectClient.getColumnmaps(), "household_id", true);
        String birthdate = checkAndConvertDateFormat(Utils.getValue(personObjectClient.getColumnmaps(), "adolescent_birthdate", true));
        String age = getAge(birthdate);
        String vcaAge = getVcaAge(birthdate);

        int plans = CasePlanDao.checkCasePlan(childId);

        int visits = VcaVisitationDao.countVisits(childId);

        String is_index = IndexPersonDao.checkIndexPerson(BaseEntityId);

        String status = IndexPersonDao.getIndexStatus(BaseEntityId);

        String is_screened = HouseholdDao.checkIfScreened(household_id);


        indexRegisterViewHolder.setupViews(firstName +" "+lastName, childId, plans, visits, is_index, status, gender, age, is_screened,vcaAge);
        indexRegisterViewHolder.itemView.setOnClickListener(onClickListener);
        indexRegisterViewHolder.itemView.findViewById(R.id.index_warning).setOnClickListener(onClickListener);
        indexRegisterViewHolder.itemView.setTag(smartRegisterClient);

    }


    private String getVcaAge(String birthdate){
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

    private String getAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today = LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);

        int years = periodBetweenDateOfBirthAndNow.getYears();
        int months = periodBetweenDateOfBirthAndNow.getMonths();

        return years + "y " + months + "m";
    }

    private String checkAndConvertDateFormat(String date){
        if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return date;
        } else {
            DateTimeFormatter oldFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate localDate = LocalDate.parse(date, oldFormatter);
                return localDate.format(newFormatter);
            } catch (DateTimeParseException e) {
                Log.e("TAG", "Invalid date format: " + e.getMessage());
                return "Invalid date format";
            }
        }
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
    public IndexRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View viewHolder = inflater().inflate(R.layout.index_register_item_layout, null);
        return new IndexRegisterViewHolder(viewHolder, context);
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
