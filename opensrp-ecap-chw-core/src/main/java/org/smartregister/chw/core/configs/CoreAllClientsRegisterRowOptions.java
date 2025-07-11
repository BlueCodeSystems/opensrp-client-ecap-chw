package org.smartregister.chw.core.configs;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.holders.CoreAllClientsRegisterViewHolder;
import org.smartregister.opd.configuration.OpdRegisterRowOptions;

public abstract class CoreAllClientsRegisterRowOptions implements OpdRegisterRowOptions<CoreAllClientsRegisterViewHolder> {

    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public boolean isCustomViewHolder() {
        return true;
    }

    @Nullable
    @Override
    public CoreAllClientsRegisterViewHolder createCustomViewHolder(@NonNull View parent) {
        return new CoreAllClientsRegisterViewHolder(parent);
    }

    @Override
    public boolean useCustomViewLayout() {
        return true;
    }

    @Override
    public int getCustomViewLayoutId() {
        return R.layout.all_client_register_list_row;
    }
}
