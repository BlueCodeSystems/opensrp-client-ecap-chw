package com.bluecodeltd.ecap.chw.util;

import android.os.Environment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.koin.test.AutoCloseKoinTest;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.Utils;

import java.util.Arrays;

import static com.bluecodeltd.ecap.chw.util.Utils.formatDateForVisual;
import static com.bluecodeltd.ecap.chw.util.Utils.getClientName;
import static com.bluecodeltd.ecap.chw.util.Utils.getFormattedDateFromTimeStamp;

public class UtilsTest extends AutoCloseKoinTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void firstCharacterUppercase_empty() {
        Assert.assertEquals("", Utils.firstCharacterUppercase(""));
    }

    @Test
    public void firstCharacterUppercase_with_one_character() {
        Assert.assertEquals("A", Utils.firstCharacterUppercase("a"));
    }

    @Test
    public void firstCharacterUppercase_with_two_word() {
        Assert.assertEquals("A b", Utils.firstCharacterUppercase("a b"));
    }

    @Test
    public void testConvertDpToPixel() {
        Assert.assertEquals(20.0, Utils.convertDpToPixel(20f, RuntimeEnvironment.application), 0);
    }

    @Test
    public void testTableColConcatEmpty() {
        Assert.assertEquals("", ChildDBConstants.tableColConcat("", ""));
    }

    @Test
    public void testTableColConcatValidInput() {
        Assert.assertEquals("table.col", ChildDBConstants.tableColConcat("table", "col"));
    }

    @Test
    public void testGetDownloadUrl() {
        String downloadUrl = BuildConfig.guidebooks_url + RuntimeEnvironment.application.getResources().getConfiguration().locale + "/fileName";
        Assert.assertEquals(downloadUrl, DownloadGuideBooksUtils.getDownloadUrl("fileName", RuntimeEnvironment.application));
    }

    @Test
    public void testHasExternalDisk() {
        Boolean canWrite = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        Assert.assertEquals(canWrite, FileUtils.hasExternalDisk());
    }

    @Test
    public void testFormatDateForVisual() {
        String date = "2020-06-23";
        String inputFormat = "yyyy-MM-dd";
        String formattedDate = formatDateForVisual(date, inputFormat);
        Assert.assertEquals(formattedDate, "23 Jun 2020");
    }

    @Test
    public void testGetClientName() {
        String name = getClientName("first_name", "middle_name", "last_name");
        if (ChwApplication.getApplicationFlavor().hasSurname())
            Assert.assertEquals("first_name middle_name last_name", name);
        else
            Assert.assertEquals("first_name middle_name", name);

    }

    @Test
    public void testGetDateTimeFromTimeStamp() {
        Assert.assertEquals("01 Dec 2020", getFormattedDateFromTimeStamp(Long.valueOf("1606780800000"), "dd MMM yyyy"));
        Assert.assertEquals("2020-12-02", getFormattedDateFromTimeStamp(Long.valueOf("1606889233342"), "yyyy-MM-dd"));

    }

    @Test
    public void testGetWFHZScore() {
        double score = com.bluecodeltd.ecap.chw.util.Utils.getWFHZScore("Male", "70", "70");
        Assert.assertNotEquals(100.0, score, 0.0);
    }

    @Test
    public void testToCSV() {
        String csv = com.bluecodeltd.ecap.chw.util.Utils.toCSV(Arrays.asList("foo", "bar", "baz"));
        Assert.assertEquals("foo, bar, baz ",csv);
    }
}
