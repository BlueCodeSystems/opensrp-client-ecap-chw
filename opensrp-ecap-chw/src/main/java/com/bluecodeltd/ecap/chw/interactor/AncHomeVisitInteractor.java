package com.bluecodeltd.ecap.chw.interactor;

import org.smartregister.chw.core.interactor.CoreAncHomeVisitInteractor;

public class AncHomeVisitInteractor extends CoreAncHomeVisitInteractor {
    public AncHomeVisitInteractor() {
        setFlavor(new AncHomeVisitInteractorFlv());
    }
}
