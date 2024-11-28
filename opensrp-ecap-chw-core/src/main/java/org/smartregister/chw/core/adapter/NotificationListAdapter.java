package org.smartregister.chw.core.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationRowViewHolder> {
    private List<Pair<String, String>> notificationRecords = new ArrayList<>();
    private View.OnClickListener onClickListener;
    public boolean canOpen = false;

    @NonNull
    @Override
    public NotificationRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_notification_row, parent, false);
        return new NotificationRowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRowViewHolder holder, int position) {
        Pair<String, String> notificationRecord = notificationRecords.get(position);
        holder.setNotificationId(notificationRecord.first);
        holder.setNotificationForText(notificationRecord.second);
    }

    public List<Pair<String, String>> getNotificationRecords() {
        return notificationRecords;
    }

    @Override
    public int getItemCount() {
        return notificationRecords.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class NotificationRowViewHolder extends RecyclerView.ViewHolder {

        private TextView notificationForTextView;
        private String notificationId;

        private NotificationRowViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationForTextView = itemView.findViewById(R.id.notification_for_textview);
            itemView.setTag(this);
            itemView.setOnClickListener(v -> onClickListener.onClick(v));
        }

        private void setNotificationForText(String notificationFor) {
            notificationForTextView.setText(itemView.getContext().getString(R.string.notification_for, notificationFor));
        }

        public String getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(String notificationId) {
            this.notificationId = notificationId;
        }
    }
}
