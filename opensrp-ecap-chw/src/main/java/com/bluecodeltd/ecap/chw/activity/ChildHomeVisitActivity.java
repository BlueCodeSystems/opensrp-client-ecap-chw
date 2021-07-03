package com.bluecodeltd.ecap.chw.activity;

import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;
import com.bluecodeltd.ecap.chw.interactor.ChildHomeVisitInteractorFlv;

public class ChildHomeVisitActivity extends CoreChildHomeVisitActivity {
    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new CoreChildHomeVisitInteractor(new ChildHomeVisitInteractorFlv()));
    }

    @Override
    public void submittedAndClose() {
        super.submittedAndClose();
        ChildProfileActivity.startMe(this, memberObject, ChildProfileActivity.class);
    }

}
