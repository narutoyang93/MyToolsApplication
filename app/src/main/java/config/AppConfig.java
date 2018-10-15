package config;

import android.os.Environment;

import com.naruto.mytoolsapplication.MyApplication;
import com.naruto.mytoolsapplication.R;


/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate ${2018-06-01}
 * @Note
 */
public final class AppConfig {
    private AppConfig() {
    }

    public static final String APP_NAME = MyApplication.getContext().getString(R.string.app_name);

    //私有文件外置存储根目录(随着卸载自动删除)
    public static final String PRIVATE_FOLDER_ROOT = MyApplication.getContext().getExternalFilesDir(null).getPath();
    //外置存储根目录
    public static final String EXTERNAL_STORAGE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    //私有文件存储目录
    public static final String PRIVATE_FOLDER_PATH = PRIVATE_FOLDER_ROOT + "/" + APP_NAME.trim()+"/";
    //公开文件存储目录
    public static final String PUBLIC_FOLDER_PATH = EXTERNAL_STORAGE_ROOT + "/" + APP_NAME.trim()+"/";
}
