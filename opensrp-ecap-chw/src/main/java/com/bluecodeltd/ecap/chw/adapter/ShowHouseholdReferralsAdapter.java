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
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.List;
import java.util.Map;

public class ShowHouseholdReferralsAdapter extends RecyclerView.Adapter<ShowHouseholdReferralsAdapter.ViewHolder> {

    Context context;

    List<ReferralModel> referrals;
    ObjectMapper oMapper;

    public ShowHouseholdReferralsAdapter(List<ReferralModel> referrals, Context context){

        super();

        this.referrals = referrals;
        this.context = context;
    }

    @Override
    public ShowHouseholdReferralsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_referral, parent, false);

        ShowHouseholdReferralsAdapter.ViewHolder viewHolder = new ShowHouseholdReferralsAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShowHouseholdReferralsAdapter.ViewHolder holder, final int position) {

        final ReferralModel showReferrals = referrals.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(showReferrals.getDate_referred());

        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                try {

                    openFormUsingFormUtils(context, "household_referral", showReferrals);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        holder.editme.setOnClickListener(v -> {

            if (v.getId() == R.id.edit_me) {

                try {

                    openFormUsingFormUtils(context, "household_referral", showReferrals);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void openFormUsingFormUtils(Context context, String formName, ReferralModel referral) throws JSONException {

        oMapper = new ObjectMapper();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", referral.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(referral, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Follow Up Visitation");
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

        return referrals.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate;

        LinearLayout linearLayout;
        ImageView editme;

        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);
            editme = itemView.findViewById(R.id.edit_me);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
