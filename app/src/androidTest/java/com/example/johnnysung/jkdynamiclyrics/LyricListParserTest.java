package com.example.johnnysung.jkdynamiclyrics;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.text.SimpleDateFormat;

public class LyricListParserTest extends ApplicationTestCase<Application> {

    private LyricListParser parser;

    public LyricListParserTest() {
        super(Application.class);
    }

    public LyricListParserTest(Class<Application> applicationClass) {
        super(applicationClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        parser = new LyricListParser();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        parser = null;
    }

    public void testLoadRawLyrics() {
        int resId=R.raw.test_lyric;
        String expected = "Hello";

        String result = parser.loadRawLyrics(getContext(), resId);

        Assert.assertEquals(expected, result);
    }

    public void testParseLyrics() {
        // arrange
        String rawText = "[00:02.16] test";
        String expected1 = "00:02.16";
        String expected2 = "test";
        SimpleDateFormat sb = new SimpleDateFormat("mm:ss.SS");

        // act
        Lyric lyric = parser.parseOneLine(rawText);

        // assert
        Assert.assertEquals(expected1, sb.format(lyric.getText()));
        Assert.assertEquals(expected2, lyric.getText());
    }
}