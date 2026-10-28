package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.util.ServiceRegister;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterStatus;

public class ServiceRegisterViewHolder extends RecyclerView.ViewHolder {

    private final RegisterRow row;
    private final String actionLabel;
    public final TextView action;

    public ServiceRegisterViewHolder(@NonNull View itemView, ServiceRegister register) {
        super(itemView);
        row = new RegisterRow(itemView).avatar(register.icon, register.iconColor, register.iconBackground);
        action = row.action;
        actionLabel = itemView.getContext().getString(register.followUpAction);
    }

    public void bind(String clientName, String id, String summaryText, ServiceRegisterStatus.Row status) {
        row.name(clientName)
                .id(id)
                .summary(summaryText)
                .detail(status.detail)
                .stripe(status.tone)
                .status(status.status, status.tone)
                .due(status.due, status.dueTone)
                .action(status.open ? actionLabel : "");
    }
}
