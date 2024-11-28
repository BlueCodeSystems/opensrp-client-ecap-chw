package org.smartregister.chw.core.presenter;

import android.util.Pair;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.domain.FamilyMember;

import java.util.ArrayList;
import java.util.List;

public class CoreFamilyChangePresenterTest extends BaseUnitTest {

    @Mock
    private FamilyChangeContract.View view;

    @Mock
    private FamilyChangeContract.Interactor interactor;

    private String familyId = "fam-entity-id";

    private CoreFamilyChangePresenter familyChangePresenter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        familyChangePresenter = new TestableCoreFamilyChangePresenter(view, familyId);
        familyChangePresenter.interactor = interactor;
    }

    @Test
    public void saveCompleted() {
        String familyHead = "family-head";
        String careGiver = "care-giver";
        familyChangePresenter.saveCompleted(familyHead, careGiver);
        Mockito.verify(view, Mockito.atLeastOnce()).saveComplete(familyHead, careGiver);
    }

    @Test
    public void getAdultMembersExcludePCG() {
        familyChangePresenter.getAdultMembersExcludePCG();
        Mockito.verify(interactor, Mockito.atLeastOnce()).getAdultMembersExcludePCG(familyId, familyChangePresenter);
    }

    @Test
    @Ignore("Fix null pointer exception")
    public void saveFamilyMember() {
        FamilyMember familyMember = new FamilyMember();
        familyMember.setFirstName("Elder");
        familyMember.setMemberID("bas1-2m");
        Pair<String, FamilyMember> familyMemberPair = Pair.create("New Fam", familyMember);
        familyChangePresenter.saveFamilyMember(RuntimeEnvironment.systemContext, familyMemberPair);
        Mockito.verify(interactor, Mockito.atLeastOnce()).updateFamilyMember(RuntimeEnvironment.systemContext, familyMemberPair, familyId, null, familyChangePresenter);
    }

    @Test
    public void renderAdultMembersExcludePCG() {
        List<FamilyMember> famList = new ArrayList<>();
        FamilyMember familyMember = new FamilyMember();
        familyMember.setFirstName("Elderly person");
        familyMember.setMemberID("bas1-2m");
        FamilyMember familyMember2 = new FamilyMember();
        familyMember.setFirstName("Small person");
        familyMember.setMemberID("bas1-4m");
        famList.add(familyMember);
        famList.add(familyMember2);
        String careGiver = "care-giver";
        String houseId = "household-id";
        familyChangePresenter.renderAdultMembersExcludePCG(famList, careGiver, houseId);
        Mockito.verify(view, Mockito.atLeastOnce()).refreshMembersView(Mockito.anyList());
    }

    @Test
    public void getAdultMembersExcludeHOF() {
        familyChangePresenter.getAdultMembersExcludeHOF();
        Mockito.verify(interactor, Mockito.atLeastOnce()).getAdultMembersExcludeHOF(familyId, familyChangePresenter);
    }

    @Test
    public void renderAdultMembersExcludeHOF() {
        List<FamilyMember> famList = new ArrayList<>();
        FamilyMember familyMember = new FamilyMember();
        familyMember.setFirstName("Elderly person");
        familyMember.setMemberID("bas1-2m");
        FamilyMember familyMember2 = new FamilyMember();
        familyMember.setFirstName("Small person");
        familyMember.setMemberID("bas1-4m");
        famList.add(familyMember);
        famList.add(familyMember2);
        String careGiver = "care-giver";
        String houseId = "household-id";
        familyChangePresenter.renderAdultMembersExcludeHOF(famList, careGiver, houseId);
        Mockito.verify(view, Mockito.atLeastOnce()).refreshMembersView(Mockito.anyList());
    }

    private static class TestableCoreFamilyChangePresenter extends CoreFamilyChangePresenter {

        public TestableCoreFamilyChangePresenter(FamilyChangeContract.View view, String familyID) {
            super(view, familyID);
        }
    }
}