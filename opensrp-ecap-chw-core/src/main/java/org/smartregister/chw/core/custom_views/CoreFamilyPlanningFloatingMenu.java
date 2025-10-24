package org.smartregister.chw.core.custom_views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.fp.custom_views.BaseFpFloatingMenu;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.chw.fp.fragment.BaseFpCallDialogFragment;

import static org.smartregister.chw.core.utils.Utils.redrawWithOption;

public class CoreFamilyPlanningFloatingMenu extends BaseFpFloatingMenu implements BottomSheetMenu {

    public FloatingActionButton fab;
    private View callLayout;
    private View referLayout;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private boolean isFabMenuOpen = false;

    private final FpMemberObject fpMemberObject;
    private OnClickFloatingMenu onClickFloatingMenu;

    public CoreFamilyPlanningFloatingMenu(Context context, FpMemberObject fpMemberObject) {
        super(context, fpMemberObject);
        this.fpMemberObject = fpMemberObject;
    }

    public void setFloatingMenuOnClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    @Override
    protected void initUi() {
        inflate(getContext(), R.layout.family_planning_floating_menu, this);
        fab = (FloatingActionButton) findViewById(R.id.family_planning_fab);
        if (fab != null) {
            fab.setOnClickListener(v -> animateFAB());
        }
        setupBottomSheet();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.family_planning_fab) {
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
        Activity activity = (Activity) this.getContext();
        BaseFpCallDialogFragment.launchDialog(activity, fpMemberObject);
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
