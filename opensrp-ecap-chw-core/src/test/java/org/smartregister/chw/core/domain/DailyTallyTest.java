package org.smartregister.chw.core.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;

public class DailyTallyTest {
    private DailyTally dailyTally = new DailyTally();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAndSetDay() {
        Date day = new Date();
        dailyTally.setDay(day);
        Assert.assertEquals(dailyTally.getDay(), day);
    }
}
