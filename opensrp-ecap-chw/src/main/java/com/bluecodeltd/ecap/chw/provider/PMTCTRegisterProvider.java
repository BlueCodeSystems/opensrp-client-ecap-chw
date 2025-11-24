package com.bluecodeltd.ecap.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.view_holder.PMTCTRegisterViewHolder;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import android.media.ToneGenerator;
import android.media.AudioManager;

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
    private static final java.util.Set<String> alertedBeep = new java.util.HashSet<>();


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
        String caregiverName = Utils.getValue(personObjectClient.getColumnmaps(), "caregiver_name", true);

        String clientId = Utils.getValue(personObjectClient.getColumnmaps(), "pmtct_id", false);
        String householdId = Utils.getValue(personObjectClient.getColumnmaps(), "household_id", true);
//
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

        String displayName = (isNullOrEmpty(firstName) && isNullOrEmpty(lastName) && !isNullOrEmpty(caregiverName))
                ? caregiverName
                : String.format("%s %s", valueOrEmpty(firstName), valueOrEmpty(lastName)).trim();
        if (isNullOrEmpty(displayName)) {
            displayName = caregiverName;
        }

        String displayId = isNullOrEmpty(clientId) ? householdId : clientId;

        pmtctRegisterViewHolder.setupViews(displayName,"ID : " + displayId, gender, age,client_type);

        // Determine unsuppressed VL flag using PMTCTMotherDao (Agyw_unsuppressed_vl_1st or Unsuppressed_vl_1st)
        boolean unsuppressed = false;
        boolean suppressed = false;
        try {
            if (clientId != null && !clientId.trim().isEmpty()) {
                PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(clientId);
                if (mother != null) {
                    String agywUnsupp = safe(mother.getAgyw_unsuppressed_vl_1st());
                    String unsupp = safe(mother.getUnsuppressed_vl_1st());
                    unsuppressed = "yes".equalsIgnoreCase(agywUnsupp) || "yes".equalsIgnoreCase(unsupp);
                    if (!unsuppressed) {
                        // Consider suppressed when explicit 'no' present or VL result fields contain 'suppressed'
                        suppressed = "no".equalsIgnoreCase(agywUnsupp) || "no".equalsIgnoreCase(unsupp)
                                || containsWord(mother.getAgyw_vl_result_1st_trimester(), "suppressed")
                                || containsWord(mother.getVl_result_1st_trimester(), "suppressed")
                                || containsWord(mother.getAgyw_vl_result_2nd_trimester(), "suppressed")
                                || containsWord(mother.getVl_result_2nd_trimester(), "suppressed")
                                || containsWord(mother.getAgyw_vl_result_3rd_trimester(), "suppressed")
                                || containsWord(mother.getVl_result_3rd_trimester(), "suppressed");
                    }
                }
            }
        } catch (Exception ignored) { }
        pmtctRegisterViewHolder.setUnsuppressedVlFlag(unsuppressed);
        pmtctRegisterViewHolder.setSuppressedVlFlag(suppressed);
        if (unsuppressed && clientId != null && !alertedBeep.contains(clientId)) {
            try {
                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP, 200);
                alertedBeep.add(clientId);
            } catch (Throwable ignored) { }
        }

        // If flagged, clicking the chip or warning toggles inline XML alert; otherwise fallback to default listener
        View flagView = pmtctRegisterViewHolder.itemView.findViewById(R.id.unsuppressed_vl_flag);
        View closeAlert = pmtctRegisterViewHolder.itemView.findViewById(R.id.btn_close_alert);
        View suppressedBtn = pmtctRegisterViewHolder.itemView.findViewById(R.id.suppressed_vl_flag);
        View.OnClickListener toggleInlineAlert = v -> pmtctRegisterViewHolder.toggleUnsuppressedAlert();
        if (flagView != null) flagView.setOnClickListener(unsuppressed ? toggleInlineAlert : null);
        if (closeAlert != null) closeAlert.setOnClickListener(v -> pmtctRegisterViewHolder.toggleUnsuppressedAlert());
        if (suppressedBtn != null) suppressedBtn.setOnClickListener(v -> suppressedBtn.setVisibility(View.GONE));
        pmtctRegisterViewHolder.itemView.setOnClickListener(onClickListener);
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
        View viewHolder = inflater().inflate(R.layout.ptcmt_register_item_layout, viewGroup, false);
        return new PMTCTRegisterViewHolder(viewHolder);
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }
    private static String valueOrEmpty(String s) { return s == null ? "" : s.trim(); }
    private static boolean containsWord(String s, String needle) {
        if (s == null) return false;
        return s.toLowerCase(java.util.Locale.ENGLISH).contains(needle.toLowerCase(java.util.Locale.ENGLISH));
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
