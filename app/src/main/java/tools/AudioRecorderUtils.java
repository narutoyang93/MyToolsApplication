package tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * 原作者：Rance on 2016/11/29 10:47
 * 邮箱：rance935@163.com
 * <p>
 * 修改：Naruto Yang 2018-10-15
 */
public class AudioRecorderUtils {
    private static final String TAG = "AudioRecorderUtils";
    private String filePath;//文件完整路径
    private String folderPath;//文件夹路径
    private String fileName;//文件名称
    private int maxDuration;//录音最长时间（单位：毫秒）
    private long startTime;
    private MediaRecorder mMediaRecorder;
    boolean isRecording = false;// 是否正在进行录制
    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    private int requestCode_permissions;
    private Context context;
    private OperationInterface permissionDeniedCallBack;
    private OperationInterface startRecordCallBack;
    private OperationInterface maxDurationReachedCallBack;//已抵达最大录音时长的回调

    /**
     * 文件存储默认sdcard/cadyd/record
     *
     * @param context
     * @param folderPath             保存文件的文件夹路径
     * @param maxDuration            录音最长时间（单位：毫秒）
     * @param permissionsRequestCode 权限请求码
     */
    public AudioRecorderUtils(final Context context, @NonNull String folderPath, int maxDuration, int permissionsRequestCode) {
        this.context = context;
        this.requestCode_permissions = permissionsRequestCode;
        File path = new File(folderPath);
        if (!path.exists())
            path.mkdirs();
        this.folderPath = folderPath;
        Log.d(TAG, "AudioRecorderUtils: folderPath=" + folderPath);
        if (maxDuration < 0) {
            throw new IllegalArgumentException("maxDuration 不能小于0");
        }
        this.maxDuration = maxDuration;
        audioStatusUpdateListener = new OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                Log.d(TAG, "onUpdate: db=" + db + ";time=" + time);
            }

            @Override
            public void onStop(int time, String filePath) {

            }

            @Override
            public void onError() {
                Toast.makeText(context, "录音过程发生异常", Toast.LENGTH_LONG).show();
            }
        };
    }


    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @param fileName 文件名称（不包含后缀）录音默认格式为ACC，所以文件名称后缀就是".acc "，不需要设置。
     * @return
     */
    public void startRecord(@NonNull String fileName) {
        //检查并申请权限
        if (!checkAndRequestPermissions(context)) {
            return;
        }

        cancelRecord();//如果已经在录音，则取消当前的录音

        Log.d(TAG, "startRecord: ");
        if (startRecordCallBack != null) {
            startRecordCallBack.done(null);
        }
        this.fileName = fileName;
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {

                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        stopRecord();
                        if (maxDurationReachedCallBack != null) {
                            maxDurationReachedCallBack.done(null);
                        }
                    }
                }
            });
        }

        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            filePath = folderPath + fileName + (fileName.endsWith(".aac") ? "" : ".aac");
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setAudioEncodingBitRate(8);
            mMediaRecorder.setMaxDuration(maxDuration);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            isRecording = true;
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
        } catch (IllegalStateException e) {
            onError(e);
        } catch (IOException e) {
            onError(e);
        }
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (!isRecording || mMediaRecorder == null)
            return 0L;
        finishRecording();
        long endTime = System.currentTimeMillis();
        int time = (int) (endTime - startTime);
        if (audioStatusUpdateListener != null) {
            audioStatusUpdateListener.onStop(time, filePath);
        }
        filePath = "";

        return endTime - startTime;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        if (!isRecording || mMediaRecorder == null)
            return;
        finishRecording();

        File file = new File(filePath);
        if (file.exists())
            file.delete();
        filePath = "";
    }


    /**
     * 结束录音
     */
    private void finishRecording() {
        isRecording = false;
        if (mMediaRecorder != null) {
            //设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.d("stopRecord", e.getMessage());
            } catch (RuntimeException e) {
                Log.d("stopRecord", e.getMessage());
            } catch (Exception e) {
                Log.d("stopRecord", e.getMessage());
            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }


    /**
     * 录音异常
     *
     * @param e
     */
    private void onError(Exception e) {
        finishRecording();
        if (audioStatusUpdateListener != null) {
            audioStatusUpdateListener.onError();
        }
        Log.d(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };


    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    public void setPermissionDeniedCallBack(OperationInterface permissionDeniedCallBack) {
        this.permissionDeniedCallBack = permissionDeniedCallBack;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setStartRecordCallBack(OperationInterface startRecordCallBack) {
        this.startRecordCallBack = startRecordCallBack;
    }

    public void setMaxDurationReachedCallBack(OperationInterface maxDurationReachedCallBack) {
        this.maxDurationReachedCallBack = maxDurationReachedCallBack;
    }

    /**
     * 检查并申请权限
     *
     * @param context
     * @return
     */
    private boolean checkAndRequestPermissions(Context context) {
        String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        return MyTools.checkPermissions((Activity) context, requestCode_permissions, permissions);
    }


    /**
     * 权限申请回调
     *
     * @param grantResults
     */
    public void permissionRequestCallBack(int[] grantResults) {
        boolean isAllGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        if (!isAllGranted) {
            if (permissionDeniedCallBack == null) {
                Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                permissionDeniedCallBack.done(null);
            }
        }
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        if (!isRecording) {
            return;
        }
        int SPACE = 100;// 间隔取样时间
        int BASE = 1;
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        public void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param time     录音时长
         * @param filePath 保存路径
         */
        public void onStop(int time, String filePath);

        /**
         * 录音失败
         */
        public void onError();
    }


    /**
     * @Purpose
     * @Author Naruto Yang
     * @CreateDate 2018/10/15
     * @Note
     */
    public interface OperationInterface {
        void done(Object o);
    }

}
