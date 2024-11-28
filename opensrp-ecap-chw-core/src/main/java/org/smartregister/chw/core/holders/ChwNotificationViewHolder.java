package org.smartregister.chw.core.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.R;

public class ChwNotificationViewHolder extends RecyclerView.ViewHolder {

    private TextView personNameAndAge;
    private TextView notificationTypeTextView;
    private TextView notificationEventDateTextView;

    public ChwNotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        personNameAndAge = itemView.findViewById(R.id.persons_name_and_age);
        notificationTypeTextView = itemView.findViewById(R.id.notification_type);
        notificationEventDateTextView = itemView.findViewById(R.id.notification_date);
    }

    public void setNameAndAge(String fullNameAndAge) {
        this.personNameAndAge.setText(fullNameAndAge);
    }

    public void setNotificationTypeTextView(String notificationType) {
        String formattedReferralType = notificationTypeTextView.getContext()
                .getString(R.string.facility_visit, notificationType);
        this.notificationTypeTextView.setText(formattedReferralType);
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationEventDateTextView.setText(notificationDate);
    }
}
