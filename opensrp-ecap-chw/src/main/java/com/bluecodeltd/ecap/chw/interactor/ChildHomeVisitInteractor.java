package com.bluecodeltd.ecap.chw.interactor;

import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;

public class ChildHomeVisitInteractor extends CoreChildHomeVisitInteractor {

    public ChildHomeVisitInteractor() {
        super(new ChildHomeVisitInteractorFlv());
    }
}
