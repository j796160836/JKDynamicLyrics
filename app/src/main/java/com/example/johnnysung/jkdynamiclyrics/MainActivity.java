package com.example.johnnysung.jkdynamiclyrics;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.lyrics_lv)
    ListView lyrics_lv;

    @InjectView(R.id.current_pos_tv)
    TextView current_pos_tv;

    @InjectView(R.id.total_tv)
    TextView total_tv;

    @InjectView(R.id.capture_btn)
    Button capture_btn;

    private LayoutInflater mInflater;
    private LyricListParser lyricListParser;
    private LyricsAdapter mAdapter;

    private ArrayList<Lyric> lyrics;
    private int currentLyric;

    private MediaPlayer mediaPlayer;
    private ScheduledExecutorService myScheduledExecutorService;

    private int mediaDuration;
    private int mediaPosition;
    private SimpleDateFormat sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        capture_btn.setOnClickListener(this);

        mInflater = LayoutInflater.from(this);

        lyricListParser = new LyricListParser();
        lyricListParser.loadLyrics(this, R.raw.lyric);
        lyrics = lyricListParser.getLyrics();

        mAdapter = new LyricsAdapter();
        lyrics_lv.setAdapter(mAdapter);

        sb = new SimpleDateFormat("mm:ss.SS");
        initPlayer();
        myScheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    private void initPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setOnErrorListener(mpErrorListener);
        mediaPlayer.setOnSeekCompleteListener(mpSeekCompleteListener);
        mediaPlayer.setOnCompletionListener(mpCompletionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        }
    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }

    private void mediaTimeUpdate() {
        if (!isFinishing() && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaDuration = mediaPlayer.getDuration();
            mediaPosition = mediaPlayer.getCurrentPosition();
            current_pos_tv.setText(sb.format(new Date(mediaPosition)));
//            current_pos_tv.setText(String.valueOf((float) mediaPosition / 1000));
            total_tv.setText(String.valueOf((float) mediaDuration / 1000));

            int index = findCurrentLyric();
            if (index != currentLyric) {
                currentLyric = index;
                mAdapter.notifyDataSetChanged();
                lyrics_lv.smoothScrollToPosition(currentLyric);
            }
        }
    }

    private int findCurrentLyric() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            long currentTimeStamp = ((Lyric) mAdapter.getItem(i)).getMilliseconds();
            if (i == mAdapter.getCount() - 1) {
                if (currentTimeStamp >= mediaPosition) {
                    return i;
                }
            } else if (i + 1 < mAdapter.getCount()) {
                long nextTimestamp = ((Lyric) mAdapter.getItem(i + 1)).getMilliseconds();
                if (currentTimeStamp >= mediaPosition && mediaPosition < nextTimestamp) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Runnable mediaTimeUpdateRunnable =
            new Runnable() {
                @Override
                public void run() {
                    mediaTimeUpdate();
                }
            };

    MediaPlayer.OnErrorListener mpErrorListener
            = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    MediaPlayer.OnSeekCompleteListener mpSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        public void onSeekComplete(MediaPlayer player) {
            player.start();
            myScheduledExecutorService.scheduleWithFixedDelay(
                    new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(mediaTimeUpdateRunnable);
                        }
                    },
                    200, //initialDelay
                    200, //delay
                    TimeUnit.MILLISECONDS);
        }
    };

    MediaPlayer.OnCompletionListener mpCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer player) {
            player.stop();
            myScheduledExecutorService.shutdown();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == capture_btn) {
            if (!isFinishing() && mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaDuration = mediaPlayer.getDuration();
                mediaPosition = mediaPlayer.getCurrentPosition();
                String timecode = sb.format(new Date(mediaPosition));
                Log.v("Timecode", timecode);
            }
        }
    }

    class LyricsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lyrics.size();
        }

        @Override
        public Object getItem(int pos) {
            return lyrics.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(int pos, View v, ViewGroup viewGroup) {
            if (v == null) {
                v = mInflater.inflate(R.layout.lyric_item, null);
            }
            TextView text = (TextView) v.findViewById(R.id.text);

            Lyric data = (Lyric) getItem(pos);
            text.setText(data.getText());


            if (currentLyric != -1 && currentLyric == pos) {
                text.setTextColor(Color.RED);
                text.setBackgroundColor(Color.parseColor("#BFFFC6"));
            } else {
                text.setTextColor(getResources().getColor(R.color.primary_material_light));
                text.setBackgroundColor(Color.TRANSPARENT);
            }

            return v;
        }
    }
}
