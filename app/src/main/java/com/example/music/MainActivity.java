package com.example.music;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button playBtn;
    private Button backBtn;
    private Button nextBtn;
    private SeekBar positionBar;
    private SeekBar volumeBar;
    private TextView elapsedTimeLabel;
    private TextView remainingTimeLabel;
    private MediaPlayer mp;
    private MediaPlayer mp2;
    private MediaPlayer mp3;
    private int totalTime;
    private MediaPlayer[] mpp = new MediaPlayer[4];
    Random random = new Random();
    private int cnt = random.nextInt(2);
    private String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = findViewById(R.id.playBtn);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);

        // Media Playerの初期化
        mp = MediaPlayer.create(this, R.raw.music);
        mp2 = MediaPlayer.create(this, R.raw.music2);
        mp3= MediaPlayer.create(this, R.raw.music3);

        mpp[0] = mp;
        mpp[1] = mp2;
        mpp[2] = mp3;

        mpp[cnt].setLooping(false);
        mpp[cnt].seekTo(0);
        mpp[cnt].setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();

        // 再生位置
        positionBar = findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    //再生位置バー
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mpp[cnt].seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        // 音量調節
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum = progress / 100f;
                        mp.setVolume(volumeNum, volumeNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // Thread (positionBar・経過時間ラベル・残り時間ラベルを更新する)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mpp[cnt] != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mpp[cnt].getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                        if(time.equals("0:00")){
                            if(cnt >= 2){
                                cnt = 0;
                            }else{
                                cnt++;
                            }
                            init();
                            musicStart(cnt);
                        }
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int currentPosition = msg.what;
            // 再生位置を更新
            positionBar.setProgress(currentPosition);

            // 経過時間ラベル更新
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            // 残り時間ラベル更新
            String remainingTime = "- " + createTimeLabel(totalTime - currentPosition);
            time = createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText(remainingTime);

            return true;
        }
    });

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


    public void playBtnClick(View view) {
        MediaPlayer nowMpp =mpp[cnt];

        if (!nowMpp.isPlaying()) {
            // 停止中
            mpp[cnt].start();
            playBtn.setBackgroundResource(R.drawable.stop);
            init();
        } else {
            // 再生中
            mpp[cnt].pause();
            playBtn.setBackgroundResource(R.drawable.play);
            init();
        }
    }

    public void nextBtnClick(View view) {
        mpp[cnt].pause();

        if(cnt >= 2){
            cnt = 0;
        }else{
            cnt++;
        }
        init();
        musicStart(cnt);

    }

    public void backBtnClick(View view) {
        mpp[cnt].pause();
        if (cnt == 0) {
            cnt = 2;
        } else {
            cnt--;
        }
        init();
        musicStart(cnt);
    }

    public void musicStart(int cnt){
        mpp[cnt].seekTo(0);
        mpp[cnt].setVolume(0.5f, 0.5f);
        totalTime = mpp[cnt].getDuration();
        mpp[cnt].start();
    }

    public void init(){
        positionBar = findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    //再生位置バー
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mpp[cnt].seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        // 音量調節
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum = progress / 100f;
                        mpp[cnt].setVolume(volumeNum, volumeNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mpp[cnt] != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mpp[cnt].getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }
}