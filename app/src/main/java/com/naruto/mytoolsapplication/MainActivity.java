package com.naruto.mytoolsapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import config.AppConfig;
import tools.AudioRecorderUtils;
import tools.WeiXinHelper;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_RECORD = 100;
    private AudioRecorderUtils audioRecorderUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test1(View view) {
        Log.d("MainActivity", "--->test1: EXTERNAL_PRIVATE_STORAGE_ROOT=" + AppConfig.EXTERNAL_PRIVATE_STORAGE_ROOT);
        Log.d("MainActivity", "--->test1: EXTERNAL_PUBLIC_STORAGE_ROOT=" + AppConfig.EXTERNAL_PUBLIC_STORAGE_ROOT);
        Log.d("MainActivity", "--->test1: EXTERNAL_STORAGE_ROOT=" + AppConfig.EXTERNAL_STORAGE_ROOT);
        Log.d("MainActivity", "--->test1: INTERNAL_PRIVATE_STORAGE_ROOT=" + AppConfig.INTERNAL_PRIVATE_STORAGE_ROOT);
    }


    public void test2(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_RECORD:
                audioRecorderUtils.permissionRequestCallBack(grantResults);
                break;
        }
    }

    public void sendText(View view) {
        WeiXinHelper.sendText("什么鬼", WeiXinHelper.Scene.FRIEND);
    }

    public void sendImage(View view) {
        getImageAndSend("http://dmimg.5054399.com/allimg/narutopic/130730d/2.jpg", new Sender() {
            @Override
            public void sendBitmap(Bitmap bitmap) {
                WeiXinHelper.sendImage(bitmap, WeiXinHelper.Scene.FRIEND);
            }
        });

    }

    public void sendWebPage(View view) {
        getImageAndSend("http://dmimg.5054399.com/allimg/narutopic/130730d/3.jpg", new Sender() {
            @Override
            public void sendBitmap(Bitmap bitmap) {
                WeiXinHelper.sendWebPage("https://blog.csdn.net/naruto_48/article/details/89441959", bitmap, WeiXinHelper.Scene.FRIEND, "ImageView的scaleType总结", "什么描述啊");
            }
        });
    }

    public static interface Sender {
        void sendBitmap(Bitmap bitmap);
    }

    private void getImageAndSend(String url, final Sender sender) {
        Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            /**
             * 成功的回调
             */
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                sender.sendBitmap(bitmap);
            }

            /**
             * 失败的回调
             */
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                sender.sendBitmap(null);
            }
        });
    }
}
