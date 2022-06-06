package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.HouseholdServiceActivity;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.GradDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.MuacDao;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.FamilyServiceModel;
import com.bluecodeltd.ecap.chw.model.GradModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HouseholdServiceAdapter extends RecyclerView.Adapter<HouseholdServiceAdapter.ViewHolder>{


    Context context;
    List<FamilyServiceModel> services;
    ObjectMapper oMapper;


    public HouseholdServiceAdapter(List<FamilyServiceModel> services, Context context){

        super();

        this.services = services;
        this.context = context;

    }

    @Override
    public HouseholdServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service, parent, false);

        HouseholdServiceAdapter.ViewHolder viewHolder = new HouseholdServiceAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HouseholdServiceAdapter.ViewHolder holder, final int position) {

        final FamilyServiceModel service = services.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(service.getDate());
        holder.txtserviceType.setText(service.getServices());

        if(service.getServices().equals("caregiver")){

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(service.getServices_caregiver());

                String[] strArr = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    strArr[i] = jsonArray.getString(i);
                }

                holder.txtServices.setText(strArr.length + " Services");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(service.getServices_household());

                String[] strArr = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    strArr[i] = jsonArray.getString(i);
                }

                holder.txtServices.setText(strArr.length + " Services");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                FormUtils formUtils = null;
                try {
                    formUtils = new FormUtils(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    openFormUsingFormUtils(context, "service_report_household", service);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void openFormUsingFormUtils(Context context, String formName, FamilyServiceModel service) throws JSONException {

        oMapper = new ObjectMapper();


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("read_only", true);

        formToBeOpened.put("entity_id", service.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(service, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Service Report");
        form.setHideSaveLabel(true);
        form.setNextLabel("Next");
        form.setPreviousLabel("Previous");
        form.setSaveLabel("Submit");
        form.setActionBarBackground(R.color.dark_grey);
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        ((Activity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);


    }

    @Override
    public int getItemCount() {

        return services.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate,txtserviceType, txtServices ;

        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            txtserviceType = itemView.findViewById(R.id.service);
            txtServices = itemView.findViewById(R.id.services);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
