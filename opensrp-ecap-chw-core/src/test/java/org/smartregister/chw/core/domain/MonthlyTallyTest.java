package org.smartregister.chw.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MonthlyTallyTest {

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private MonthlyTally monthlyTally = new MonthlyTally();
    private Date date;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        date = sdf.parse("2020-02-08");
    }

    @Test
    public void testGetDateAndSetDateSent() {
        monthlyTally.setDateSent(date);
        Assert.assertEquals(monthlyTally.getDateSent(), date);
    }

    @Test
    public void testGetAndSetMonth() {
        monthlyTally.setMonth(date);
        Assert.assertEquals(monthlyTally.getMonth(), date);
    }

    @Test
    public void testIsAndSetEdited() {
        monthlyTally.setEdited(true);
        Assert.assertTrue(monthlyTally.isEdited());
    }

    @Test
    public void testGetAndSetProviderId() {
        String providerId = "123";
        monthlyTally.setProviderId(providerId);
        Assert.assertEquals(monthlyTally.getProviderId(), providerId);
    }

    @Test
    public void testGetAndSetUpdatedAt() {
        monthlyTally.setUpdatedAt(date);
        Assert.assertEquals(monthlyTally.getUpdatedAt(), date);
    }

    @Test
    public void testGetAndSetCreatedAt() {
        monthlyTally.setCreatedAt(date);
        Assert.assertEquals(monthlyTally.getCreatedAt(), date);
    }

}