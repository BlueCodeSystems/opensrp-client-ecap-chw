package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdServiceActivity;
import com.bluecodeltd.ecap.chw.activity.VcaServiceActivity;
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.List;
import java.util.Map;

public class VCAServiceAdapter  extends RecyclerView.Adapter<VCAServiceAdapter.ViewHolder>{

    Context context;
    List<VCAServiceModel> services;
    ObjectMapper oMapper;


    public VCAServiceAdapter(List<VCAServiceModel> services, Context context){

        super();

        this.services = services;
        this.context = context;

    }

    @Override
    public VCAServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service, parent, false);

        VCAServiceAdapter.ViewHolder viewHolder = new VCAServiceAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VCAServiceAdapter.ViewHolder holder, final int position) {

        final VCAServiceModel service = services.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(service.getDate());
        holder.txtserviceType.setText("For VCA");


        JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(service.getServices());

                String[] strArr = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    strArr[i] = jsonArray.getString(i);
                }

                holder.txtServices.setText(strArr.length + " Services");

            } catch (JSONException e) {
                e.printStackTrace();
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
                    openFormUsingFormUtils(context, "service_report_vca", service);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void openFormUsingFormUtils(Context context, String formName, VCAServiceModel service) throws JSONException {

        oMapper = new ObjectMapper();


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("read_only", true);

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
