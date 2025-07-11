package org.smartregister.chw.core.holders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.R;


public class RegisterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewParentName;
    public TextView textViewChildName;
    public TextView textViewReferralDay;
    public TextView textViewAddressGender;
    public TextView textViewChildAge;
    public Button dueButton;
    public View dueButtonLayout;
    public View childColumn;
    public ImageView goToProfileImage;
    public View goToProfileLayout;

    public RegisterViewHolder(View itemView) {
        super(itemView);

        textViewParentName = itemView.findViewById(R.id.textview_parent_name);
        textViewChildName = itemView.findViewById(R.id.text_view_child_name);
        textViewAddressGender = itemView.findViewById(R.id.text_view_address_gender);
        textViewReferralDay = itemView.findViewById(R.id.text_view_referral_day);
        textViewChildAge = itemView.findViewById(R.id.text_view_child_age);
        dueButton = itemView.findViewById(R.id.due_button);
        dueButtonLayout = itemView.findViewById(R.id.due_button_wrapper);
        goToProfileImage = itemView.findViewById(R.id.go_to_profile_image_view);
        goToProfileLayout = itemView.findViewById(R.id.go_to_profile_layout);

        childColumn = itemView.findViewById(R.id.child_column);
    }
}
