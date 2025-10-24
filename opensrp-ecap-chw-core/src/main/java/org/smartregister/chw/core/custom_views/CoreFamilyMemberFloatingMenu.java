package org.smartregister.chw.core.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;

public abstract class CoreFamilyMemberFloatingMenu extends LinearLayout implements View.OnClickListener, BottomSheetMenu {
    public FloatingActionButton fab;
    public OnClickFloatingMenu onClickFloatingMenu;
    private View callLayout;
    private View referLayout;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private boolean isFabMenuOpen = false;

    public CoreFamilyMemberFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    public CoreFamilyMemberFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public CoreFamilyMemberFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    public void initUi() {
        inflate(getContext(), R.layout.view_individual_floating_menu, this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setupBottomSheet();
        fab.setOnClickListener(v -> animateFAB());
    }

    public View getCallLayout() {
        return callLayout;
    }

    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
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

    @Override
    public void onClick(View v) {
        if (onClickFloatingMenu != null) {
            onClickFloatingMenu.onClickMenu(v.getId());
        }
        dismissMenu();
    }

    public abstract void reDraw(boolean has_phone);

    public void hideFab() {
        fab.hide();
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
