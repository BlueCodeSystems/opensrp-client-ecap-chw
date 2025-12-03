package com.bluecodeltd.ecap.chw.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

/**
 * Adds defensive scaffolding for BaseRegisterFragment-based screens.
 * Ensures required views (toolbar/recycler/progress) exist before delegating to the base setup.
 */
public abstract class BaseSafeRegisterFragment extends BaseRegisterFragment {

    @Override
    public void setupViews(View view) {
        try {
            ensureBaseScaffold(view);
        } catch (Throwable t) {
            Timber.w(t, "ensureBaseScaffold failed; continuing");
        }
        try {
            super.setupViews(view);
        } catch (Throwable t) {
            Timber.w(t, "Base setupViews threw; continuing");
        }
    }

    protected void ensureBaseScaffold(View root) {
        if (!(root instanceof ViewGroup)) return;
        ensureToolbar(root);
        ensureRecycler(root);
        ensureProgress(root);
    }

    protected void ensureToolbar(View root) {
        Toolbar tb = null;
        try { tb = root.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
        if (tb == null) {
            try {
                Toolbar newTb = new Toolbar(requireContext());
                newTb.setId(org.smartregister.R.id.register_toolbar);
                ViewGroup parent = (ViewGroup) root;
                // Insert at top if LinearLayout; otherwise append
                if (parent instanceof android.widget.LinearLayout) {
                    parent.addView(newTb, 0);
                } else {
                    parent.addView(newTb, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(56, getActivity())
                    ));
                }
            } catch (Throwable t) {
                Timber.w(t, "Failed to create toolbar");
            }
        }
    }

    protected void ensureRecycler(View root) {
        RecyclerView rv = null;
        try { rv = root.findViewById(org.smartregister.R.id.recycler_view); } catch (Exception ignored) {}
        if (rv == null) { try { rv = root.findViewById(com.bluecodeltd.ecap.chw.R.id.recycler_view); } catch (Exception ignored) {} }
        if (rv == null) {
            try {
                RecyclerView newRv = new RecyclerView(requireContext());
                newRv.setId(org.smartregister.R.id.recycler_view);
                if (newRv.getLayoutManager() == null) newRv.setLayoutManager(new LinearLayoutManager(getContext()));
                ViewGroup parent = (ViewGroup) root;
                parent.addView(newRv, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
            } catch (Throwable t) {
                Timber.w(t, "Failed to create recycler_view");
            }
        }
    }

    protected void ensureProgress(View root) {
        ProgressBar pb = null;
        try { pb = root.findViewById(com.bluecodeltd.ecap.chw.R.id.client_list_progress); } catch (Exception ignored) {}
        if (pb == null) {
            try {
                ProgressBar newPb = new ProgressBar(requireContext());
                newPb.setId(com.bluecodeltd.ecap.chw.R.id.client_list_progress);
                ViewGroup parent = (ViewGroup) root;
                if (parent instanceof android.widget.RelativeLayout) {
                    android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    lp.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT, android.widget.RelativeLayout.TRUE);
                    parent.addView(newPb, lp);
                } else {
                    parent.addView(newPb, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                }
                newPb.setVisibility(View.GONE);
            } catch (Throwable t) {
                Timber.w(t, "Failed to create client_list_progress");
            }
        }
    }

    protected Toolbar setupSupportActionBarIfPresent(View root) {
        Toolbar tb = null;
        try { tb = root.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
        if (tb != null && getActivity() instanceof AppCompatActivity) {
            try {
                tb.setContentInsetsAbsolute(0, 0);
                tb.setContentInsetsRelative(0, 0);
                tb.setContentInsetStartWithNavigation(0);
            } catch (Throwable ignored) {}
            AppCompatActivity act = (AppCompatActivity) getActivity();
            try { act.setSupportActionBar(tb); } catch (Throwable ignored) {}
            return tb;
        }
        return tb;
    }
}

