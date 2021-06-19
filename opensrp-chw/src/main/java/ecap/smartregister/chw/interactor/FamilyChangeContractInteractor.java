package ecap.smartregister.chw.interactor;

import ecap.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;

public class FamilyChangeContractInteractor extends CoreFamilyChangeContractInteractor {

    public FamilyChangeContractInteractor() {
        setCoreChwApplication();
        setFlavour();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = ChwApplication.getInstance();
    }

    @Override
    protected void setFlavour() {
        this.flavor = new FamilyChangeContractInteractorFlv();
    }
}
