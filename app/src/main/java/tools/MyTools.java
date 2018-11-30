package tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naruto.mytoolsapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MyTools {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 注意：看仔细了，dip2px(x)!=dip2px(-x)
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 注意：看仔细了，px2dip(x)!=px2dip(-x)
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    // 检查设备是否连接网络
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // 检查设备是否连接wifi
    public static boolean isConnectedWithWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }


    /**
     * GPS是否已打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsOpen(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsOpen = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsOpen;
    }

    /**
     * 检查并申请权限
     *
     * @param activity
     * @param permissionsRequestCode
     * @param permissions
     * @return 是否已经授权，无需申请
     */
    public static boolean checkPermissions(Activity activity, int permissionsRequestCode, String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        List<String> requestPermissionsList = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsList.add(p);
            }
        }
        if (!requestPermissionsList.isEmpty()) {
            String[] requestPermissionsArray = requestPermissionsList.toArray(new String[requestPermissionsList.size()]);
            ActivityCompat.requestPermissions(activity, requestPermissionsArray, permissionsRequestCode);
            return false;
        } else {
            return true;
        }
    }


    /**
     * @Purpose 默认弹窗点击事件处理接口
     * @Author Naruto Yang
     * @CreateDate 2018/10/12
     * @Note
     */
    public static class DefaultClickListener implements View.OnClickListener {
        private Dialog dialog;
        private View.OnClickListener onClickListener;

        public DefaultClickListener(Dialog dialog, View.OnClickListener onClickListener) {
            this.dialog = dialog;
            this.onClickListener = onClickListener;
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

}
