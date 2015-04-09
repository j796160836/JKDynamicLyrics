package com.example.johnnysung.jkdynamiclyrics;

import java.util.Date;

/**
 * Created by johnnysung on 2015/04/09.
 */
public class Lyric {
    private Date time;
    private String text;

    public Lyric() {

    }

    public Lyric(Date time, String text) {
        this.time = time;
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "time=" + time +
                ", text='" + text + '\'' +
                '}';
    }
}
