package ecap.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import ecap.smartregister.chw.fragment.FamilyRemoveMemberFragment;

public class FamilyRemoveMemberActivity extends CoreFamilyRemoveMemberActivity {

    @Override
    protected void setRemoveMemberFragment() {
        this.removeMemberFragment = FamilyRemoveMemberFragment.newInstance(getIntent().getExtras());
    }

}
