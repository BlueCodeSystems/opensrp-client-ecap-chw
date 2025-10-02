package org.smartregister.chw.anc.presenter;

import org.smartregister.chw.anc.contract.BaseHomeVisitImmunizationFragmentContract;

import java.lang.ref.WeakReference;
import java.util.Map;

public class BaseHomeVisitImmunizationFragmentPresenter implements BaseHomeVisitImmunizationFragmentContract.Presenter {

    private BaseHomeVisitImmunizationFragmentContract.Model model;
    private WeakReference<BaseHomeVisitImmunizationFragmentContract.View> view;

    public BaseHomeVisitImmunizationFragmentPresenter(BaseHomeVisitImmunizationFragmentContract.View view, BaseHomeVisitImmunizationFragmentContract.Model model) {
        this.model = model;
        this.view = new WeakReference<>(view);

        initialize();
    }

    private void initialize() {
        if (view.get() != null) {
            model.getVaccinesState(view.get().getJsonObject(), this);
        }
    }

    @Override
    public void onNoVaccineState(boolean state) {
        if (view.get() != null) {
            view.get().updateNoVaccineCheck(state);
        }
    }

    @Override
    public void onSelectedVaccinesInitialized(Map<String, String> selectedVaccines, boolean variedMode) {
        if (view.get() != null) {
            view.get().updateSelectedVaccines(selectedVaccines, variedMode);
            view.get().setSingleEntryMode(!variedMode);
        }
    }

    @Override
    public void onNoVaccineSelected() {
        if (view.get() != null) {
            view.get().noVaccineGivenMode();
        }
    }
}
