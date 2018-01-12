package com.patelheggere.voicerecorder.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.patelheggere.voicerecorder.R;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AudioRecorder extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private Button srt, stp, pstrt, pstp, resume, pause;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private MediaPlayer mPlayer = null;
    private boolean isPause = false;
    private String[] files;
    private String target = null;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        files = new String[2];
        srt = findViewById(R.id.startbtn);
        stp = findViewById(R.id.stopbtn);
        pstrt = findViewById(R.id.strtplay);
        pstp = findViewById(R.id.stpplay);
        resume = findViewById(R.id.resume);
        pause = findViewById(R.id.pause);
        target = getExternalCacheDir().getAbsolutePath()+"audiorecordtest.3gp";;

        // Record to the external cache directory for visibility


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        srt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startRecording();
            }
        });
        stp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(mRecorder!=null) {
                  mRecorder.stop();
                  mRecorder.release();
                  mRecorder = null;
              }
                  mergeMediaFiles(true, files, target);

            }
        });

        pstrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });
        pstp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        System.out.println("stopped playing");
    }
    private void stopRecording() {
        if(null!=mRecorder) {
            try{
                mRecorder.stop();
                mRecorder = null;
            }catch(RuntimeException ex){
               ex.printStackTrace();
            }

            System.out.println("stopped recoed");
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(target);
            mPlayer.prepare();
            mPlayer.start();
            System.out.println("Started playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    private void startRecording() {
        mRecorder = null;
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/"+new Date().getTime()+"audiorecordtest.3gp";
       // File file =  new File(mFileName);
        files[i++]= mFileName;
        System.out.println(mFileName);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("LOG TAG", "prepare() failed");
        }
        mRecorder.start();
        System.out.println("Started recoed");
    }


    public static boolean mergeMediaFiles(boolean isAudio, String sourceFiles[], String targetFile) {
        try {
            String mediaKey = isAudio ? "soun" : "vide";
            List<Movie> listMovies = new ArrayList<>();
            for (int i=0; i<sourceFiles.length; i++) {

                    listMovies.add(MovieCreator.build(sourceFiles[i].toString()));

            }
            for (int i = 0; i <listMovies.size() ; i++) {
                System.out.println("Files:"+listMovies.get(i));
            }
            List<Track> listTracks = new LinkedList<>();
            for (Movie movie : listMovies) {
                for (Track track : movie.getTracks()) {
                    if (track.getHandler().equals(mediaKey)) {
                        listTracks.add(track);
                    }
                }
            }
            Movie outputMovie = new Movie();
            if (!listTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(outputMovie);
            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rws").getChannel();
            container.writeContainer(fileChannel);
            fileChannel.close();
            System.out.println("dfdg:"+targetFile);
            return true;
        }
        catch (IOException e) {
            Log.e("MYTAG", "Error merging media files. exception: "+e.getMessage());
            return false;
        }
    }

}
