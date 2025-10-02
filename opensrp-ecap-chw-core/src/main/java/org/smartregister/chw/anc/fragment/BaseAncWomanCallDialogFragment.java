package org.smartregister.chw.anc.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.contract.BaseAncWomanCallDialogContract;
import org.smartregister.chw.anc.listener.BaseAncWomanCallWidgetDialogListener;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.opensrp_chw_anc.R;

import static android.view.View.GONE;
import static org.smartregister.util.Utils.getName;

public class BaseAncWomanCallDialogFragment extends DialogFragment implements BaseAncWomanCallDialogContract.View {

    public static final String DIALOG_TAG = "BaseAncCallWidgetDialogFragment_DIALOG_TAG";
    private static String ancWomanName, ancWomanPhoneNumber, ancFamilyHeadName, ancFamilyHeadPhone, womanProfileType;
    private View.OnClickListener listener = null;

    public static BaseAncWomanCallDialogFragment launchDialog(Activity activity, String womanName, String ancWomanPhone, String familyHeadName, String familyHeadPhone, String profileType) {
        BaseAncWomanCallDialogFragment dialogFragment = BaseAncWomanCallDialogFragment.newInstance();
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(DIALOG_TAG);
        ancWomanPhoneNumber = ancWomanPhone;
        ancWomanName = womanName;
        ancFamilyHeadName = familyHeadName;
        ancFamilyHeadPhone = familyHeadPhone;
        womanProfileType = profileType;
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, DIALOG_TAG);

        return dialogFragment;
    }

    public static BaseAncWomanCallDialogFragment newInstance() {
        return new BaseAncWomanCallDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ChwTheme_Dialog_FullWidth);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.anc_member_call_widget_dialog_fragment, container, false);
        setUpPosition();
        TextView callTittleTextView = dialogView.findViewById(R.id.call_title);
        TextView callTitle = dialogView.findViewById(R.id.call_anc_woman_title);

        if (womanProfileType.equals(Constants.MEMBER_PROFILE_TYPES.PNC)) {
            callTittleTextView.setText(getString(R.string.call_pnc_woman));
            callTitle.setText(getString((R.string.call_pnc_woman)));
        }

        if (listener == null) {
            listener = new BaseAncWomanCallWidgetDialogListener(this);
        }

        initUI(dialogView);
        return dialogView;
    }

    private void initUI(ViewGroup rootView) {

        if (StringUtils.isNotBlank(ancWomanPhoneNumber)) {
            TextView ancWomanNameTextView = rootView.findViewById(R.id.call_anc_woman_name);
            ancWomanNameTextView.setText(ancWomanName);

            TextView ancCallAncWomanPhone = rootView.findViewById(R.id.call_anc_woman_phone);
            ancCallAncWomanPhone.setTag(ancWomanPhoneNumber);
            ancCallAncWomanPhone.setText(getName(getCurrentContext().getString(R.string.anc_call), ancWomanPhoneNumber));
            ancCallAncWomanPhone.setOnClickListener(listener);
        } else {

            rootView.findViewById(R.id.layout_anc_woman).setVisibility(GONE);
        }

        if (StringUtils.isNotBlank(ancFamilyHeadPhone)) {
            TextView familyHeadName = rootView.findViewById(R.id.anc_call_head_name);
            familyHeadName.setText(ancFamilyHeadName);

            TextView ancCallHeadPhone = rootView.findViewById(R.id.anc_call_head_phone);
            ancCallHeadPhone.setTag(ancFamilyHeadPhone);
            ancCallHeadPhone.setText(getName(getCurrentContext().getString(R.string.anc_call), ancFamilyHeadPhone));
            ancCallHeadPhone.setOnClickListener(listener);

        } else {

            rootView.findViewById(R.id.anc_layout_family_head).setVisibility(GONE);
        }

        rootView.findViewById(R.id.anc_call_close).setOnClickListener(listener);
    }

    protected void setUpPosition() {
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        p.y = 20;
        getDialog().getWindow().setAttributes(p);
    }

    @Override
    public Context getCurrentContext() {
        return getActivity();
    }

    @Override
    public BaseAncWomanCallDialogContract.Dialer getPendingCallRequest() {
        return null;
    }

    @Override
    public void setPendingCallRequest(BaseAncWomanCallDialogContract.Dialer dialer) {
//        Implement pending call request
//        BaseAncWomanCallDialogContract.Dialer mDialer = dialer;
    }
}
