package org.smartregister.chw.core.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.contract.CoreChildRegisterFragmentContract;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreChildRegisterFragmentPresenterTest {

    @Mock
    private CoreChildRegisterFragmentContract.View view;

    @Mock
    private CoreChildRegisterFragmentContract.Model model;

    private CoreChildRegisterFragmentPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = Mockito.mock(CoreChildRegisterFragmentPresenter.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(presenter, "viewReference", new WeakReference<>(view));
        ReflectionHelpers.setField(presenter, "model", model);
        ReflectionHelpers.setField(presenter, "viewConfigurationIdentifier", "12345");
    }

    @Test
    public void testProcessViewConfigurations() {
        ViewConfiguration viewConfiguration = Mockito.mock(ViewConfiguration.class);
        Mockito.doReturn(viewConfiguration).when(model).getViewConfiguration("12345");

        Set<View> changes = new HashSet<>();
        Mockito.doReturn(changes).when(model).getRegisterActiveColumns("12345");
        Mockito.doReturn(Mockito.mock(RegisterConfiguration.class)).when(viewConfiguration).getMetadata();

        presenter.processViewConfigurations();
        Assert.assertEquals(ReflectionHelpers.getField(presenter, "visibleColumns"), changes);
    }

    @Test
    public void testInitializeQueries() {
        presenter.initializeQueries("sample");
        Mockito.verify(view).initializeQueryParams(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(view).initializeAdapter(Mockito.any());
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

    @Test
    public void testUpdateSortAndFilter() {
        List<Field> filterList = new ArrayList<>();
        Field sortField = Mockito.mock(Field.class);
        presenter.updateSortAndFilter(filterList, sortField);
        Mockito.verify(view).updateFilterAndFilterStatus(Mockito.any(), Mockito.any());
    }

    @Test
    public void testGetMainCondition() {
        String condition = " ec_child.date_removed is null AND  ((( julianday('now') - julianday(ec_child.dob))/365.25) <5)   and (( ifnull(ec_child.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  and ((( julianday('now') - julianday(ec_child.dob))/365.25) < 5) ";
        Assert.assertEquals(condition, presenter.getMainCondition());
    }

    @Test
    public void testGetMainConditionWithTable() {
        String condition = " tableName.date_removed is null AND  ((( julianday('now') - julianday(ec_child.tableName.dob))/365.25) <5)   and (( ifnull(ec_child.tableName.entry_point,'') <> 'PNC' ) or (ifnull(ec_child.tableName.entry_point,'') = 'PNC' and ( date(ec_child.tableName.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.tableName.mother_entity_id ) = 0)))  or (ifnull(ec_child.entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))  and ((( julianday('now') - julianday(ec_child.tableName.dob))/365.25) < 5) ";
        String tableName = "tableName";
        Assert.assertEquals(condition, presenter.getMainCondition(tableName));
    }
}
