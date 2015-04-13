package com.example.johnnysung.jkdynamiclyrics;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by johnnysung on 2015/04/08.
 */
public class LyricListParser {
    private ArrayList<Lyric> lyrics;

    public LyricListParser() {
        lyrics = new ArrayList<>();
    }

    public String loadRawLyrics(Context context, int res) {
        String oneLine;
        StringBuilder sb;
        sb = new StringBuilder();
        try {
            InputStream raw = context.getResources().openRawResource(res);
            BufferedReader br = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            while ((oneLine = br.readLine()) != null) {
                System.out.println(oneLine);
                sb.append(oneLine);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void loadLyrics(Context context, int res) {
        lyrics.clear();
        String oneLine;
        try {
            InputStream raw = context.getResources().openRawResource(res);
            BufferedReader br = new BufferedReader(new InputStreamReader(raw, "UTF8"));
            while ((oneLine = br.readLine()) != null) {
                Lyric lyric = parseOneLine(oneLine);
                lyrics.add(lyric);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Lyric parseOneLine(String str) {
        int start = str.indexOf("[");
        int end = str.indexOf("]");
        String text = null;
        Date date = null;
        if (start != -1 && end != -1) {
            SimpleDateFormat sb = new SimpleDateFormat("mm:ss.SS");
            sb.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                date = sb.parse(str.substring(start + 1, end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            text = str.substring(end + 1).trim();
            return new Lyric(date, text);
        }
        return null;
    }

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }
}
