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
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;
import com.bluecodeltd.ecap.chw.model.WeGroupDataCollectionModel;
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

public class WeGroupDataCollectionAdapter extends RecyclerView.Adapter<WeGroupDataCollectionAdapter.ViewHolder> {

    private List<WeGroupDataCollectionModel> dataList;
    private Context context;
    ObjectMapper oMapper;
    public interface OnDataUpdateListener {
        void onDataUpdate();
    }
    private WeGroupDataCollectionAdapter.OnDataUpdateListener onDataUpdateListener;

    public void setOnDataUpdateListener(WeGroupDataCollectionAdapter.OnDataUpdateListener onDataUpdateListener) {
        this.onDataUpdateListener = onDataUpdateListener;
    }

    public WeGroupDataCollectionAdapter(Context context, List<WeGroupDataCollectionModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_collection_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeGroupDataCollectionModel dataItem = dataList.get(position);

        holder.reports.setText(dataItem.getDate_data_collection());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openFormUsingFormUtils(context, "we_group_data_collection_form", dataItem);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openFormUsingFormUtils(Context context, String formName, WeGroupDataCollectionModel dataCollectionModel) throws JSONException {

        oMapper = new ObjectMapper();


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", dataCollectionModel.getBase_entity_id());

        CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(dataCollectionModel, Map.class));

        startFormActivity(formToBeOpened);

    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Data Collection Report");
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
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reports;
       LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            reports = itemView.findViewById(R.id.report_date);
            linearLayout = itemView.findViewById(R.id.itemm);
        }
    }

    public void updateData(List<WeGroupDataCollectionModel> newData) {
        if (newData != null) {
            dataList.clear();
            dataList.addAll(newData);
            notifyDataSetChanged();
        }
    }
}
