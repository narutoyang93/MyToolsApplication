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

    //私有文件内置存储目录(随着卸载自动删除,无需在 Manifest 文件中或者动态申请外部存储空间的文件读写权限。)
    public static final String INTERNAL_PRIVATE_STORAGE_ROOT = MyApplication.getContext().getFilesDir().getPath();
    //私有文件外置存储目录(随着卸载自动删除，如果是扩展卡上的不会删除，4.4及以上无需在 Manifest 文件中或者动态申请外部存储空间的文件读写权限。/storage/emulated/0/Android/data/[包名]/files)
    public static final String EXTERNAL_PRIVATE_STORAGE_ROOT = MyApplication.getContext().getExternalFilesDir(null).getPath();
    //外置存储根目录(/storage/emulated/0)
    public static final String EXTERNAL_STORAGE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    //公有文件外置存储目录(/storage/emulated/0/Download)
    public static final String EXTERNAL_PUBLIC_STORAGE_ROOT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

}