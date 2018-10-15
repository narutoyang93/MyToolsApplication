package com.naruto.mytoolsapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import config.AppConfig;
import tools.AudioRecorderUtils;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_RECORD = 100;
    private AudioRecorderUtils audioRecorderUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test1(View view) {
        if (audioRecorderUtils == null) {
            audioRecorderUtils = new AudioRecorderUtils(this, AppConfig.PUBLIC_FOLDER_PATH, 60000, PERMISSIONS_REQUEST_CODE_RECORD);
        }
        audioRecorderUtils.startRecord("abc123");
    }


    public void test2(View view) {
        if (audioRecorderUtils != null) {
            audioRecorderUtils.stopRecord();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQUEST_CODE_RECORD:
                audioRecorderUtils.permissionRequestCallBack(grantResults);
                break;
        }
    }
}
