package org.smartregister.chw.core.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;

public class SendMonthlyDraftDialogFragment extends DialogFragment {
    private String date;
    private String month;
    private View.OnClickListener onSendClickedListener;

    public static SendMonthlyDraftDialogFragment newInstance(String month, String date, View.OnClickListener onSendClickedListener) {
        SendMonthlyDraftDialogFragment f = new SendMonthlyDraftDialogFragment();
        f.setMonth(month);
        f.setDate(date);
        f.setOnSendClickedListener(onSendClickedListener);

        return f;
    }

    private void setDate(String date) {
        this.date = date;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    private void setOnSendClickedListener(View.OnClickListener onSendClickedListener) {
        this.onSendClickedListener = onSendClickedListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String provider = CoreChwApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
        View view = inflater.inflate(
                R.layout.dialog_fragment_send_monthly,
                container, false);
        TextView tvSendMonthlyDraft = view.findViewById(R.id.tv_send_monthly_draft);
        tvSendMonthlyDraft.setText(String.format(
                getString(R.string.send_report_warning),
                month,
                date,
                provider));

        Button cancelButton = view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> SendMonthlyDraftDialogFragment.this.dismiss());

        Button sendButton = view.findViewById(R.id.button_send);
        sendButton.setOnClickListener(v -> {
            SendMonthlyDraftDialogFragment.this.dismiss();
            if (onSendClickedListener != null) {
                onSendClickedListener.onClick(v);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(() -> {
            Window window = null;
            if (getDialog() != null) {
                window = getDialog().getWindow();
            }

            if (window == null) {
                return;
            }

            window.setLayout(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
        });
    }
}
