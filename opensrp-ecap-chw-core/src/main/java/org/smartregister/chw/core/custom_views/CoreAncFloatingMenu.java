package org.smartregister.chw.core.custom_views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.anc.custom_views.BaseAncFloatingMenu;
import org.smartregister.chw.anc.fragment.BaseAncWomanCallDialogFragment;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;

import static org.smartregister.chw.core.utils.Utils.redrawWithOption;

public abstract class CoreAncFloatingMenu extends BaseAncFloatingMenu implements BottomSheetMenu {
    public FloatingActionButton fab;
    protected View referLayout;
    protected View callLayout;
    protected BottomSheetDialog bottomSheetDialog;
    protected View bottomSheetView;
    protected boolean isFabMenuOpen = false;
    protected OnClickFloatingMenu onClickFloatingMenu;

    public CoreAncFloatingMenu(Context context, String ancWomanName, String ancWomanPhone,
                               String ancFamilyHeadName, String ancFamilyHeadPhone, String profileType) {
        super(context, ancWomanName, ancWomanPhone, ancFamilyHeadName, ancFamilyHeadPhone, profileType);
    }

    public CoreAncFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFloatMenuClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    protected void initUi() {
        inflate(getContext(), R.layout.view_anc_call_woma_floating_menu, this);
        fab = (FloatingActionButton) findViewById(R.id.anc_fab);
        if (fab != null) {
            fab.setOnClickListener(v -> animateFAB());
        }
        setupBottomSheet();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.anc_fab) {
            animateFAB();
            return;
        }

        if (onClickFloatingMenu != null) {
            onClickFloatingMenu.onClickMenu(view.getId());
        }
        dismissMenu();
    }

    public void animateFAB() {
        if (bottomSheetDialog == null) {
            setupBottomSheet();
        }
        if (bottomSheetDialog == null) {
            return;
        }

        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        } else {
            bottomSheetDialog.show();
            isFabMenuOpen = true;
        }
    }

    public void launchCallWidget() {
        Activity activity = (Activity) getContext();
        BaseAncWomanCallDialogFragment.launchDialog(activity, getWomanName(),
                getPhoneNumber(), getFamilyHeadName(), getFamilyHeadPhone(), getWomanProfileType());
    }

    public void redraw(boolean hasPhoneNumber) {
        redrawWithOption(this, hasPhoneNumber);
    }

    public View getCallLayout() {
        return callLayout;
    }

    private void setupBottomSheet() {
        if (bottomSheetDialog != null) {
            return;
        }
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.ChwBottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.menu_call_refer_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetView = sheetView;

        callLayout = sheetView.findViewById(R.id.call_layout);
        referLayout = sheetView.findViewById(R.id.refer_to_facility_layout);

        if (callLayout != null) {
            callLayout.setOnClickListener(this);
        }
        if (referLayout != null) {
            referLayout.setOnClickListener(this);
        }

        bottomSheetDialog.setOnShowListener(dialog -> {
            isFabMenuOpen = true;
            if (fab != null) {
                fab.setImageResource(R.drawable.ic_input_add);
            }
        });

        bottomSheetDialog.setOnDismissListener(dialog -> {
            isFabMenuOpen = false;
            if (fab != null) {
                fab.setImageResource(com.vijay.jsonwizard.R.drawable.ic_edit_white);
            }
        });
    }

    private void dismissMenu() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    @Override
    public View getBottomSheetView() {
        return bottomSheetView;
    }
}
