package com.bluecodeltd.ecap.chw.view_holder;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;

public class ServiceRegisterViewHolder extends RecyclerView.ViewHolder {

    private final TextView nameTextView;
    private final TextView genderAgeTextView;
    private final TextView detailTextView;
    private final TextView idTextView;

    public ServiceRegisterViewHolder(@NonNull View itemView, ServiceRegister register) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.familyNameTextView);
        genderAgeTextView = itemView.findViewById(R.id.gender_age);
        detailTextView = itemView.findViewById(R.id.service_register_detail);
        idTextView = itemView.findViewById(R.id.villageTextView);

        int iconColor = ContextCompat.getColor(itemView.getContext(), register.iconColor);
        ColorStateList iconTint = ColorStateList.valueOf(iconColor);
        itemView.findViewById(R.id.service_register_accent).setBackgroundColor(iconColor);
        itemView.findViewById(R.id.service_register_avatar).setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), register.iconBackground)));
        ImageView icon = itemView.findViewById(R.id.service_register_icon);
        icon.setImageResource(register.icon);
        icon.setImageTintList(iconTint);
        detailTextView.setTextColor(iconColor);
        TextView footer = itemView.findViewById(R.id.service_register_footer);
        footer.setText(register.title);
        footer.setTextColor(iconColor);
    }

    public void setupViews(String name, String genderAge, String detail, String clientId) {
        nameTextView.setText(name);
        genderAgeTextView.setText(genderAge);
        detailTextView.setText(detail);
        detailTextView.setVisibility(detail.isEmpty() ? View.GONE : View.VISIBLE);
        idTextView.setText(clientId);
        idTextView.setVisibility(clientId.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
