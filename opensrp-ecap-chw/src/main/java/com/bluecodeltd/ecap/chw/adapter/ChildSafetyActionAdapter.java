package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ChildSafetyPlanActions;
import com.bluecodeltd.ecap.chw.model.ChildSafetyActionModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChildSafetyActionAdapter extends RecyclerView.Adapter<ChildSafetyActionAdapter.ViewHolder>{

    Context context;

    List<ChildSafetyActionModel> action_plan;

    ObjectMapper oMapper;

    public ChildSafetyActionAdapter(ArrayList<ChildSafetyActionModel> action_plan, Context context){

        super();

        this.action_plan = action_plan;
        this.context = context;
    }

    @Override
    public ChildSafetyActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_action_plan, parent, false);

        ChildSafetyActionAdapter.ViewHolder viewHolder = new ChildSafetyActionAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChildSafetyActionAdapter.ViewHolder holder, final int position) {

        final ChildSafetyActionModel plan = action_plan.get(position);

        holder.txtSafetyThreats.setText(plan.getSafety_threats());
        holder.txtSafetyAction.setText(plan.getSafety_action());
        holder.txtSafetyProtection.setText(plan.getSafety_protection());
        holder.txtWhen.setText(plan.getStateWhen());
        holder.txtFrequency.setText(plan.getFrequency());
        holder.txtWho.setText(plan.getWho());
        holder.txtActionDate.setText("Date Created : " + plan.getInitial_date());


        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                holder.exPandableView.setVisibility(View.VISIBLE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.VISIBLE);
            }
        });

        holder.expMore.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_more) {

                holder.exPandableView.setVisibility(View.VISIBLE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.VISIBLE);
            }
        });

        holder.editme.setOnClickListener(v -> {

            if (v.getId() == R.id.edit_me) {

                try {
                    if(context instanceof ChildSafetyPlanActions){
                        openFormUsingFormUtils(context, "child_safety_action", plan);
                    } else {
                        openFormUsingFormUtils(context, "caregiver_domain", plan);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.expLess.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_less) {

                holder.exPandableView.setVisibility(View.GONE);
                holder.expMore.setVisibility(View.VISIBLE);
                holder.expLess.setVisibility(View.GONE);
            }
        });

    }

    public void openFormUsingFormUtils(Context context, String formName, ChildSafetyActionModel caseplan) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", caseplan.getBase_entity_id());

//        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("read_only",true);

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(caseplan, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Vulnerabilities");
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

        return action_plan.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView  txtSafetyThreats,txtSafetyAction,txtSafetyProtection,txtWhen,txtFrequency,txtWho,txtActionDate;

        LinearLayout linearLayout, exPandableView;

        ImageView expMore, expLess, editme;

        public ViewHolder(View itemView) {

            super(itemView);

            editme = itemView.findViewById(R.id.edit_me);
            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            linearLayout = itemView.findViewById(R.id.itemm);
            exPandableView = itemView.findViewById(R.id.expandable);
            txtSafetyThreats = itemView.findViewById(R.id.safetyThreats);
            txtSafetyAction = itemView.findViewById(R.id.safetyAction);
            txtSafetyProtection = itemView.findViewById(R.id.safetyProtection);
            txtWhen = itemView.findViewById(R.id.when);
            txtFrequency = itemView.findViewById(R.id.frequency);
            txtWho = itemView.findViewById(R.id.who);
            txtActionDate = itemView.findViewById(R.id.initial_date);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }

}