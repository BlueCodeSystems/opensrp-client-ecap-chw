package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;
import com.bluecodeltd.ecap.chw.view_holder.ServiceRegisterViewHolder;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

public class ServiceRegisterProvider implements RecyclerViewProvider<ServiceRegisterViewHolder> {

    private final Context context;
    private final ServiceRegister register;
    private final View.OnClickListener onClickListener;
    private final View.OnClickListener paginationViewHandler;

    public ServiceRegisterProvider(Context context, ServiceRegister register, View.OnClickListener onClickListener, View.OnClickListener paginationViewHandler) {
        this.context = context;
        this.register = register;
        this.onClickListener = onClickListener;
        this.paginationViewHandler = paginationViewHandler;
    }

    private static String getAge(String birthdate) {
        try {
            Period age = Period.between(LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("dd-MM-u")), LocalDate.now());
            if (age.getYears() > 0) {
                return age.getYears() + (age.getYears() == 1 ? " Year Old" : " Years Old");
            } else if (age.getMonths() > 0) {
                return age.getMonths() + (age.getMonths() == 1 ? " Month Old" : " Months Old");
            }
            return age.getDays() + " Days Old";
        } catch (Exception e) {
            return "Age Not Set";
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, ServiceRegisterViewHolder viewHolder) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) smartRegisterClient;
        Map<String, String> columns = client.getColumnmaps();

        String name = (Utils.getValue(columns, "first_name", true) + " "
                + Utils.getValue(columns, "last_name", true)).trim();
        String gender = Utils.getValue(columns, "gender", true);
        String birthdate = Utils.getValue(columns, "birthdate", false);
        String genderAge = gender + " : " + (StringUtils.isNotBlank(birthdate) ? getAge(birthdate) : "Age Not Set");

        String detailValue = Utils.getValue(columns, register.detailColumn, true);
        String secondaryValue = Utils.getValue(columns, register.secondaryColumn, true);
        String detail = "";
        if (StringUtils.isNotBlank(detailValue)) {
            detail = register.detailLabel + ": " + detailValue
                    + (StringUtils.isNotBlank(secondaryValue) ? " · " + secondaryValue : "");
        }

        String clientNumber = Utils.getValue(columns, "client_number", false);
        String clientId = StringUtils.isNotBlank(clientNumber) ? "ID : " + clientNumber : "";

        viewHolder.setupViews(name, genderAge, detail, clientId);
        viewHolder.itemView.setOnClickListener(onClickListener);
        viewHolder.itemView.setTag(smartRegisterClient);
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
    public ServiceRegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View view = inflater().inflate(R.layout.service_register_item_layout, viewGroup, false);
        return new ServiceRegisterViewHolder(view, register);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater().inflate(org.smartregister.malaria.R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }
}
