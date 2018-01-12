package com.patelheggere.voicerecorder.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.patelheggere.voicerecorder.Helper.PauseResumeAudioRecorder;
import com.patelheggere.voicerecorder.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PauseResumeAudioRecorder mediaRecorder;

    private Button recordButton;
    private Button pauseButton;
    private Button stopButton;
    private Button resumeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaRecorder=new PauseResumeAudioRecorder();
        mediaRecorder.setAudioFile(Environment.getExternalStorageDirectory() + "/Sample1");

        recordButton=(Button)findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);
        pauseButton=(Button)findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(this);
        stopButton=(Button)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);
        resumeButton=(Button)findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(this);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mediaRecorder.getCurrentState()==PauseResumeAudioRecorder.RECORDING_STATE || mediaRecorder.getCurrentState()==PauseResumeAudioRecorder.PAUSED_STATE) {
            mediaRecorder.stopRecording();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pauseButton:
                mediaRecorder.pauseRecording();
                break;
            case R.id.recordButton:
                mediaRecorder.startRecording();
                break;
            case R.id.resumeButton:
                mediaRecorder.resumeRecording();
                break;
            case R.id.stopButton:
                mediaRecorder.stopRecording();
                break;
        }
    }
}
