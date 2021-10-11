package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

public class SubmittedFormsLIstAdapter extends BaseAdapter {
    private Context context;
    String[] formName;
    int[] FormIcon;
    private LayoutInflater inflater = null;

    public SubmittedFormsLIstAdapter(Context context, String[] formName, int[] formIcon) {
        this.context = context;
        this.formName = formName;
        FormIcon = formIcon;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return formName.length;
    }

    @Override
    public Object getItem(int i) {
        return formName[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View formView = view;

        if (formView == null)
        {
            formView =inflater.inflate(R.layout.submitted_forms_list,null);
        }
        TextView formNameTextView = formView.findViewById(R.id.submittedFormsLabel);
        ImageView formIcon = formView.findViewById(R.id.submittedFormsImageView);

        formNameTextView.setText(formName[i]);
        formIcon.setBackgroundResource(FormIcon[i]);
        return formView;
    }
}
