package org.smartregister.chw.core.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.listener.MemberAdapterListener;

import java.util.Arrays;
import java.util.List;

public class MemberAdapterTest extends BaseUnitTest {
    @Mock
    private MemberAdapter.MyViewHolder viewHolder;
    private MemberAdapter adapter;
    private Context context = RuntimeEnvironment.application;
    private List<FamilyMember> myDataset;
    private MemberAdapterListener memberAdapterListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        FamilyMember familyMember = new FamilyMember();
        familyMember.setDob("21-09-2000");
        familyMember.setFamilyHead(true);
        familyMember.setGender("Female");
        familyMember.setMemberID("2020022");
        familyMember.setFirstName("jane");
        familyMember.setMiddleName("kenna");
        familyMember.setEduLevel("University");
        familyMember.setLastName("Kenya");
        familyMember.setEverSchool("Yes");
        familyMember.setSchoolLevel("Higher");
        familyMember.setPhone("290380-83080390-93");
        familyMember.setOtherPhone("67987983-3787494-87798");

        FamilyMember familyMember2 = new FamilyMember();
        familyMember2.setDob("22-01-2010");
        familyMember2.setFamilyHead(true);
        familyMember2.setGender("Female");
        familyMember2.setMemberID("20233332");
        familyMember2.setFirstName("King");
        familyMember2.setMiddleName("Kenanah");

        memberAdapterListener = Mockito.spy(MemberAdapterListener.class);
        myDataset = Arrays.asList(familyMember, familyMember2);
        adapter = new MemberAdapter(context, myDataset, memberAdapterListener);
    }

    @Test
    public void testOnBindViewHolder() {
        TextView tvName = Mockito.mock(TextView.class);
        TextView tvGender = Mockito.mock(TextView.class);
        RadioButton radioButton = Mockito.mock(RadioButton.class);
        LinearLayout llQuestions = Mockito.mock(LinearLayout.class);
        View view = Mockito.mock(View.class);
        EditText etPhone = Mockito.mock(EditText.class);
        EditText etAlternatePhone = Mockito.mock(EditText.class);


        ReflectionHelpers.setField(viewHolder, "tvName", tvName);
        ReflectionHelpers.setField(viewHolder, "tvGender", tvGender);
        ReflectionHelpers.setField(viewHolder, "llQuestions", llQuestions);
        ReflectionHelpers.setField(viewHolder, "radioButton", radioButton);
        ReflectionHelpers.setField(viewHolder, "view", view);
        ReflectionHelpers.setField(viewHolder, "etPhone", etPhone);
        ReflectionHelpers.setField(viewHolder, "etAlternatePhone", etAlternatePhone);

        FamilyMember model = myDataset.get(0);
        adapter.onBindViewHolder(viewHolder, 0);

        String fullname = model.getFirstName().concat(" ").concat(model.getMiddleName()).concat(" ").concat(model.getLastName()).concat(",").concat(" ");
        Mockito.verify(tvName).setText(fullname);
        Mockito.verify(tvGender).setText(model.getGender());
        Mockito.verify(llQuestions).setVisibility(View.GONE);
        Mockito.verify(radioButton).setChecked(false);
        Mockito.verify(etPhone).setText(model.getPhone());
        Mockito.verify(etAlternatePhone).setText(model.getOtherPhone());

    }

    @Test
    public void phoneInputFieldsShowByDefault() {
        MemberAdapter.Flavor flavor = () -> false;
        ViewGroup viewGroup = Mockito.mock(ViewGroup.class, Mockito.CALLS_REAL_METHODS);
        Mockito.when(viewGroup.getContext()).thenReturn(context);
        MemberAdapter.MyViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        adapter.setFlavor(flavor);
        adapter.onBindViewHolder(viewHolder, 0);
        viewHolder.radioButton.setChecked(true);
        Assert.assertEquals(View.VISIBLE, viewHolder.llQuestions.getVisibility());
    }

    @Test
    public void settingShowPhoneInputFieldsFalseHidesFields() {
        MemberAdapter.Flavor flavor = new MemberAdapter.Flavor() {
            @Override
            public boolean isPhoneNumberLength16Digit() {
                return false;
            }

            @Override
            public boolean showPhoneNumberInputFields() {
                return false;
            }
        };
        ViewGroup viewGroup = Mockito.mock(ViewGroup.class, Mockito.CALLS_REAL_METHODS);
        Mockito.when(viewGroup.getContext()).thenReturn(context);
        MemberAdapter.MyViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        adapter.setFlavor(flavor);
        adapter.onBindViewHolder(viewHolder, 0);
        Assert.assertEquals(View.GONE, viewHolder.llQuestions.getVisibility());
    }

    @Test
    public void testSetSelected() {
        String selected = "selected";
        adapter.setSelected(viewHolder, selected);
        if (memberAdapterListener != null) {
            Mockito.verify(memberAdapterListener).onMenuChoiceChange();
        }
    }

    @Test
    public void getItemCount() {
        Assert.assertEquals(2, myDataset.size());
    }

    @Test
    public void getSelected() {
        String selected = null;
        Assert.assertEquals(adapter.getSelected(), selected);
    }
}