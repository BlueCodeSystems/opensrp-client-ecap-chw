package org.smartregister.chw.core.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.FamilyCallDialogContract;
import org.smartregister.chw.core.event.PermissionEvent;
import org.smartregister.chw.core.listener.CallWidgetDialogListener;
import org.smartregister.chw.core.presenter.FamilyCallDialogPresenter;
import org.smartregister.util.PermissionUtils;

public class FamilyCallDialogFragment extends DialogFragment implements FamilyCallDialogContract.View {
    public static final String DIALOG_TAG = "FamilyCallWidgetDialogFragment_DIALOG_TAG";
    private View.OnClickListener listener = null;
    private FamilyCallDialogContract.Dialer mDialer;
    protected String familyBaseEntityId;
    protected LinearLayout llFamilyHead;
    protected TextView tvFamilyHeadTitle;
    protected TextView tvFamilyHeadName;
    protected TextView tvFamilyHeadPhone;
    protected LinearLayout llCareGiver;
    protected TextView tvCareGiverTitle;
    protected TextView tvCareGiverName;
    protected TextView tvCareGiverPhone;
    protected TextView dialogTitle;

    public static FamilyCallDialogFragment launchDialog(Activity activity, String familyBaseEntityId) {
        FamilyCallDialogFragment dialogFragment = FamilyCallDialogFragment.newInstance(familyBaseEntityId);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, DIALOG_TAG);

        return dialogFragment;
    }

    public static FamilyCallDialogFragment newInstance(String familyBaseEntityId) {
        FamilyCallDialogFragment familyCallDialogFragment = new FamilyCallDialogFragment();
        familyCallDialogFragment.setFamilyBaseEntityId(familyBaseEntityId);
        return familyCallDialogFragment;
    }

    protected void setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, org.smartregister.chw.opensrp_chw_anc.R.style.ChwTheme_Dialog_FullWidth);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.family_call_widget_dialog_fragment, container, false);
        setUpPosition();

        if (listener == null) {
            listener = new CallWidgetDialogListener(this);
        }

        initUI(dialogView);
        initializePresenter();
        return dialogView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getPendingCallRequest() == null) {
            EventBus.getDefault().unregister(this);
        }
        listener = null;
    }

    private void setUpPosition() {
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        p.y = 20;
        getDialog().getWindow().setAttributes(p);
    }

    private void initUI(ViewGroup rootView) {

        llFamilyHead = rootView.findViewById(R.id.layout_family_head);
        tvFamilyHeadTitle = rootView.findViewById(R.id.call_head_title);
        tvFamilyHeadName = rootView.findViewById(R.id.call_head_name);
        tvFamilyHeadPhone = rootView.findViewById(R.id.call_head_phone);

        llCareGiver = rootView.findViewById(R.id.layout_caregiver);
        tvCareGiverTitle = rootView.findViewById(R.id.call_caregiver_title);
        tvCareGiverName = rootView.findViewById(R.id.call_caregiver_name);
        tvCareGiverPhone = rootView.findViewById(R.id.call_caregiver_phone);
        dialogTitle = rootView.findViewById(R.id.call_title);

        rootView.findViewById(R.id.close).setOnClickListener(listener);
        tvFamilyHeadPhone.setOnClickListener(listener);
        tvCareGiverPhone.setOnClickListener(listener);
    }

    @Override
    public void refreshHeadOfFamilyView(FamilyCallDialogContract.Model model) {
        if (model != null && StringUtils.isNotBlank(model.getPhoneNumber())) {
            llFamilyHead.setVisibility(View.VISIBLE);
            tvFamilyHeadName.setText(model.getName());

            tvFamilyHeadPhone.setText(String.format(getString(R.string.call_prompt), model.getPhoneNumber()));
            tvFamilyHeadPhone.setTag(model.getPhoneNumber());

            tvFamilyHeadTitle.setText(model.getRole());
            if (model.isIndependent()) {
                tvFamilyHeadTitle.setVisibility(View.GONE);
                dialogTitle.setText(R.string.call_all_clients_member);
            }
        } else {
            llFamilyHead.setVisibility(View.GONE);
            tvFamilyHeadName.setText("");
            tvFamilyHeadPhone.setText("");
            tvFamilyHeadPhone.setTag(null);
            tvFamilyHeadTitle.setText("");
        }
    }

    @Override
    public void refreshCareGiverView(FamilyCallDialogContract.Model model) {
        if (model != null && (StringUtils.isNotBlank(model.getPhoneNumber())
                || (model.isIndependent() && StringUtils.isBlank(model.getOtherContact().second)))) {
            llCareGiver.setVisibility(View.VISIBLE);

            if (model.isIndependent()) {
                String otherPhoneNumber = model.getOtherContact().second;
                String caregiver = model.getOtherContact().first;
                if (StringUtils.isBlank(caregiver) || StringUtils.isBlank(otherPhoneNumber)) {
                    llCareGiver.setVisibility(View.GONE);
                }
                tvCareGiverPhone.setText(String.format(getString(R.string.call_prompt), otherPhoneNumber));
                tvCareGiverPhone.setTag(otherPhoneNumber);
                tvCareGiverName.setText(caregiver);
                tvCareGiverTitle.setText(R.string.care_giver);
            } else {
                tvCareGiverPhone.setText(String.format(getString(R.string.call_prompt), model.getPhoneNumber()));
                tvCareGiverPhone.setTag(model.getPhoneNumber());
                tvCareGiverName.setText(model.getName());
                tvCareGiverTitle.setText(model.getRole());
            }

        } else {
            llCareGiver.setVisibility(View.GONE);
            tvCareGiverName.setText("");
            tvCareGiverPhone.setText("");
            tvCareGiverPhone.setTag(null);
            tvCareGiverTitle.setText("");
        }
    }

    @Override
    public FamilyCallDialogContract.Dialer getPendingCallRequest() {
        return mDialer;
    }

    @Override
    public void setPendingCallRequest(FamilyCallDialogContract.Dialer dialer) {
        mDialer = dialer;
    }

    @Override
    public FamilyCallDialogContract.Presenter initializePresenter() {
        return new FamilyCallDialogPresenter(this, familyBaseEntityId);
    }

    @Override
    public Context getCurrentContext() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getContext();
        } else {
            return getActivity().getApplicationContext();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPermissionEvent(PermissionEvent permissionEvent) {
        if (permissionEvent.getPermissionType() == PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (permissionEvent.isGranted()) {
                if (getPendingCallRequest() != null) {
                    getPendingCallRequest().callMe();
                    setPendingCallRequest(null); // delete pending request
                    return;
                }
                EventBus.getDefault().unregister(this);
                return;
            }
            EventBus.getDefault().unregister(this);
        }
    }

}
