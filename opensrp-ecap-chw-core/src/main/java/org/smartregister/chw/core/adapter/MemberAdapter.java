package org.smartregister.chw.core.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.listener.MemberAdapterListener;
import org.smartregister.chw.core.utils.Utils;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
    public String TAG = MemberAdapter.class.getCanonicalName();
    private List<FamilyMember> familyMembers;
    private MyViewHolder currentViewHolder;
    private Context context;
    private String selected = null;
    private Animation slideUp;
    private Animation slideDown;
    private MemberAdapterListener memberAdapterListener;
    private Flavor flavor;

    public MemberAdapter(@NonNull Context context, List<FamilyMember> myDataset, MemberAdapterListener memberAdapterListener) {
        familyMembers = myDataset;
        this.context = context;
        this.memberAdapterListener = memberAdapterListener;
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }

    @NonNull
    @Override
    public MemberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.change_member_list, parent, false);
        return new MemberAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final FamilyMember model = familyMembers.get(position);

        String dobString = Utils.getDuration(model.getDob());
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

        if (StringUtils.isNotBlank(model.getDod())) {
            dobString = Utils.getDuration(model.getDod(), model.getDob());
            dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        }
        holder.tvGender.setText(Utils.getGenderLanguageSpecific(context, model.getGender()));
        holder.tvName.setText(String.format("%s, %s", model.getFullNames(), dobString));
        holder.llQuestions.setVisibility(model.getMemberID().equals(selected) && flavor.showPhoneNumberInputFields() ? View.VISIBLE : View.GONE);
        holder.radioButton.setChecked(model.getMemberID().equals(selected));

        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                redrawView(holder, model);
            }
        });
        holder.view.setOnClickListener(v -> redrawView(holder, model));
        renderViews(holder, model);
    }

    private void redrawView(MyViewHolder holder, FamilyMember model) {
        if (currentViewHolder != null) {
            currentViewHolder.radioButton.setChecked(false);
            currentViewHolder.llQuestions.setVisibility(View.GONE);
            currentViewHolder.llQuestions.startAnimation(slideUp);
        }

        setSelected(holder, model.getMemberID());

        boolean isVisible = (holder.llQuestions.getVisibility() == View.VISIBLE);
        if (model.getMemberID().equals(selected) && !isVisible && flavor.showPhoneNumberInputFields()) {
            holder.llQuestions.setVisibility(View.VISIBLE);
            holder.llQuestions.startAnimation(slideDown);
        }

        if (model.getMemberID().equals(selected) && isVisible) {
            holder.llQuestions.setVisibility(View.GONE);
            holder.llQuestions.startAnimation(slideUp);
        }

        holder.radioButton.setChecked(model.getMemberID().equals(selected));
    }

    private void renderViews(final MyViewHolder holder, FamilyMember model) {
        holder.etPhone.setText(model.getPhone());
        if (!TextUtils.isEmpty(model.getOtherPhone())) {
            holder.etAlternatePhone.setText(model.getOtherPhone());
        }
    }

    public void setSelected(MyViewHolder view, String selected) {
        currentViewHolder = view;
        this.selected = selected;
        if (memberAdapterListener != null) {
            memberAdapterListener.onMenuChoiceChange();
        }
    }

    @Override
    public int getItemCount() {
        return familyMembers.size();
    }

    public boolean validateSave() {
        if (currentViewHolder == null) {
            return false;
        }
        boolean res = true;
        if (flavor.showPhoneNumberInputFields()) {
            res = flavor.isPhoneNumberValid(currentViewHolder.etPhone, currentViewHolder.etAlternatePhone);
            if (!res) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(context.getString(R.string.change_member_alert));
                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.setPositiveButton(
                        context.getString(R.string.dismiss),
                        (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }

        return res;

    }

    public FamilyMember getSelectedResults() {

        for (FamilyMember m : familyMembers) {
            if (m.getMemberID().equals(getSelected())) {

                m.setPhone(currentViewHolder.etPhone.getText().toString());
                m.setOtherPhone(currentViewHolder.etAlternatePhone.getText().toString());
                return m;
            }
        }

        return null;
    }

    public String getSelected() {
        return selected;
    }

    public interface Flavor {
        boolean isPhoneNumberLength16Digit();

        default boolean isPhoneNumberValid(EditText phoneEditText, EditText alternatePhoneEditText) {
            return phoneEditText.getText().length() != 0 || alternatePhoneEditText.length() != 0;
        }

        default boolean showPhoneNumberInputFields() {
            return true;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvGender;
        public RadioButton radioButton;
        public LinearLayout llQuestions, llNewPhone, llAltPhone;
        public View view;
        public EditText etPhone, etAlternatePhone;

        private MyViewHolder(View view) {
            super(view);
            this.view = view;

            tvName = view.findViewById(R.id.tvName);
            tvGender = view.findViewById(R.id.tvGender);
            radioButton = view.findViewById(R.id.rbButton);
            llQuestions = view.findViewById(R.id.llQuestions);

            llNewPhone = view.findViewById(R.id.llNewNumber);
            llAltPhone = view.findViewById(R.id.llOtherNumber);

            etPhone = view.findViewById(R.id.etPhoneNumber);
            etAlternatePhone = view.findViewById(R.id.etOtherNumber);

        }

    }

}