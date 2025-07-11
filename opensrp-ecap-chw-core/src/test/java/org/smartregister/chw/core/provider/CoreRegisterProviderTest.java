package org.smartregister.chw.core.provider;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import net.sqlcipher.MatrixCursor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.impl.CoreRegisterProviderImpl;
import org.smartregister.chw.core.shadows.ContextShadow;
import org.smartregister.chw.core.shadows.UtilsShadowUtil;
import org.smartregister.commonregistry.CommonRepository;

import java.util.List;
import java.util.Map;

@Config(shadows = {UtilsShadowUtil.class, ContextShadow.class})
public class CoreRegisterProviderTest extends BaseUnitTest {

    private CoreRegisterProvider coreRegisterProvider;
    private final Context context = RuntimeEnvironment.application;

    @Mock
    private View.OnClickListener onClickListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreRegisterProvider = Mockito.mock(CoreRegisterProviderImpl.class, Mockito.CALLS_REAL_METHODS);
        Whitebox.setInternalState(coreRegisterProvider, "onClickListener", onClickListener);
    }

    @Test
    public void canSetVisitNotDoneButtonState() {
        Button button = Mockito.mock(Button.class);
        coreRegisterProvider.setVisitNotDone(context, button);
        Mockito.verify(button).setTextColor(context.getColor(R.color.progress_orange));
        Mockito.verify(button).setText(context.getString(R.string.visit_not_done));
        Mockito.verify(button).setBackgroundColor(context.getResources().getColor(R.color.transparent));
        Mockito.verify(button).setOnClickListener(null);
    }

    @Test
    public void canSetVisitAboveTwentyFourButtonState() {
        Button button = Mockito.mock(Button.class);
        coreRegisterProvider.setVisitAboveTwentyFourView(context, button);
        Mockito.verify(button).setTextColor(context.getColor(R.color.alert_complete_green));
        Mockito.verify(button).setText(context.getString(R.string.visit_done));
        Mockito.verify(button).setBackgroundColor(context.getResources().getColor(R.color.transparent));
        Mockito.verify(button).setOnClickListener(null);
    }

    @Test
    public void canSetVisitOverdueButtonState() {
        Button button = Mockito.mock(Button.class);
        String lastVisitDays = "10 days";
        coreRegisterProvider.setVisitButtonOverdueStatus(context, button, lastVisitDays);
        Mockito.verify(button).setTextColor(context.getResources().getColor(R.color.white));
        Mockito.verify(button).setText(context.getString(R.string.due_visit, lastVisitDays));
        Mockito.verify(button).setBackgroundResource(R.drawable.overdue_red_btn_selector);
        Mockito.verify(button).setOnClickListener(onClickListener);
    }

    @Test
    public void canSetVisitDueButtonState() {
        Button button = Mockito.mock(Button.class);
        coreRegisterProvider.setVisitButtonDueStatus(context, button);
        Mockito.verify(button).setTextColor(context.getColor(R.color.alert_in_progress_blue));
        Mockito.verify(button).setText(context.getString(R.string.record_home_visit));
        Mockito.verify(button).setBackgroundResource(R.drawable.blue_btn_selector);
        Mockito.verify(button).setOnClickListener(onClickListener);
    }

    @Test
    public void getChildrenReturnsCorrectMap() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id","base_entity_id", "gender", "last_home_visit", "visit_not_done",
                "date_created", "dob", "entry_point", "mother_entity_id"});
        matrixCursor.addRow(new Object[]{"child-1-id","test-entity-123", "Male", "2021-06-15", "0", "2021-06-29", "2020-01-11", "PNC", "mother-id-123"});
        matrixCursor.addRow(new Object[]{"child-2-id","test-entity-124", "Female", "2021-06-15", "0", "2021-06-29", "2019-02-20", "PNC", "mother-id-123"});
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        ContextShadow.setTestCommonRepository(commonRepository);
        String queryString = "Select ec_child.id as _id , ec_child.base_entity_id , ec_child.gender , " +
                "ec_child.last_home_visit , ec_child.visit_not_done , ec_child.date_created , ec_child.dob , " +
                "ec_child.entry_point , ec_child.mother_entity_id FROM ec_child INNER JOIN ec_family_member " +
                "ON  ec_child.base_entity_id =  ec_family_member.base_entity_id WHERE  ec_child.date_removed is null " +
                "AND ec_child.relational_id = 'test-family-entity-id' AND  ((( julianday('now') - julianday(ec_child.dob))/365.25) <5)   " +
                "and (( ifnull(ec_child.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.entry_point,'') = 'PNC' " +
                "and ( date(ec_child.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member " +
                "WHERE base_entity_id = ec_child.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  " +
                "and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  " +
                "and ((( julianday('now') - julianday(ec_child.dob))/365.25) < 5)    ORDER BY ec_child.dob ASC  ";
        Mockito.doReturn(matrixCursor).when(commonRepository).queryTable(queryString);
        List<Map<String, String>> childrenList = coreRegisterProvider.getChildren("test-family-entity-id");
        Assert.assertNotNull(childrenList);
        Assert.assertEquals(2, childrenList.size());
        Assert.assertEquals("child-1-id", childrenList.get(0).get("_id"));
        Assert.assertEquals("test-entity-123", childrenList.get(0).get("base_entity_id"));
        Assert.assertEquals("child-2-id", childrenList.get(1).get("_id"));
        Assert.assertEquals("test-entity-124", childrenList.get(1).get("base_entity_id"));
    }
}
