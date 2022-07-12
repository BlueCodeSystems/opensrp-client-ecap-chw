package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ChildSafetyPlanActions;
import com.bluecodeltd.ecap.chw.model.ChildSafetyPlanModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Map;

public class ChildSafetyPlanAdapter  extends RecyclerView.Adapter<ChildSafetyPlanAdapter.ViewHolder>{

    Context context;
    ArrayList<ChildSafetyPlanModel> plans;
    ObjectMapper oMapper;


    public ChildSafetyPlanAdapter(ArrayList<ChildSafetyPlanModel> plans, Context context){

        super();

        this.plans = plans;
        this.context = context;

    }

    @Override
    public ChildSafetyPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_safety_plan, parent, false);

        ChildSafetyPlanAdapter.ViewHolder viewHolder = new ChildSafetyPlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildSafetyPlanAdapter.ViewHolder holder, final int position) {

        final ChildSafetyPlanModel plan = plans.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(plan.getInitial_date());

        holder.linearLayout.setOnClickListener(v -> {
            Intent i = new Intent(context, ChildSafetyPlanActions.class);
            Bundle bundle = new Bundle();
            bundle.putString("child_ID",plan.getUnique_id());
            bundle.putString("action_date",plan.getInitial_date());
            i.putExtras(bundle);
            context.startActivity(i);
        });
    }

    public void openFormUsingFormUtils(Context context, String formName, ChildSafetyPlanModel service) throws JSONException {

        oMapper = new ObjectMapper();


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).remove("read_only");

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

        return plans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate;

        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);


        }


        @Override
        public void onClick(View v) {

        }
    }
}
