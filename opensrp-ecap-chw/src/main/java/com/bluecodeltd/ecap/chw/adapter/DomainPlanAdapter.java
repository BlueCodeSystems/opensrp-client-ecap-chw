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
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
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

public class DomainPlanAdapter extends RecyclerView.Adapter<DomainPlanAdapter.ViewHolder>{

    Context context;

    List<CasePlanModel> caseplans;

    ObjectMapper oMapper;

    public DomainPlanAdapter(ArrayList<CasePlanModel> caseplans, Context context){

        super();

        this.caseplans = caseplans;
        this.context = context;
    }

    @Override
    public DomainPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_domain, parent, false);

        DomainPlanAdapter.ViewHolder viewHolder = new DomainPlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DomainPlanAdapter.ViewHolder holder, final int position) {

        final CasePlanModel casePlan = caseplans.get(position);

        holder.txtType.setText(casePlan.getType());
        holder.txtVulnerability.setText(casePlan.getVulnerability());
        holder.txtGoal.setText(casePlan.getGoal());
        holder.txtServices.setText(casePlan.getServices());
        holder.txtServicesReferred.setText(casePlan.getService_referred());
        holder.txtInstitution.setText(casePlan.getInstitution());
        holder.txtDueDate.setText("Due Date : " + casePlan.getDue_date());


        if(casePlan.getStatus().equals(("C"))){

            holder.txtStatus.setText("Complete");

        } else if(casePlan.getStatus().equals(("P"))) {

            holder.txtStatus.setText("In Progress");

        } else if(casePlan.getStatus().equals(("D"))) {

            holder.txtStatus.setText("Delayed");

        }

        holder.txtComment.setText(casePlan.getComment());

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
                    if(context instanceof CasePlan){
                        openFormUsingFormUtils(context, "domain", casePlan);
                    } else {
                        openFormUsingFormUtils(context, "caregiver_domain", casePlan);
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

    public void openFormUsingFormUtils(Context context, String formName, CasePlanModel caseplan) throws JSONException {

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

        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("read_only",true);

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

        return caseplans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtType, txtVulnerability,txtCasePlanStatus,
                txtGoal, txtServices, txtServicesReferred, txtInstitution, txtDueDate, txtStatus, txtComment;

        LinearLayout linearLayout, exPandableView;

        ImageView expMore, expLess, editme;

        public ViewHolder(View itemView) {

            super(itemView);

            editme = itemView.findViewById(R.id.edit_me);
            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            linearLayout = itemView.findViewById(R.id.itemm);
            exPandableView = itemView.findViewById(R.id.expandable);
            txtType = itemView.findViewById(R.id.typex);
            txtVulnerability = itemView.findViewById(R.id.vulnerability);
            txtGoal = itemView.findViewById(R.id.goal);
            txtServices = itemView.findViewById(R.id.services);
            txtServicesReferred = itemView.findViewById(R.id.services_referred);
            txtInstitution = itemView.findViewById(R.id.institution);
            txtDueDate = itemView.findViewById(R.id.due_date);
            txtStatus = itemView.findViewById(R.id.statusx);
            txtComment = itemView.findViewById(R.id.comment);


        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }

}
