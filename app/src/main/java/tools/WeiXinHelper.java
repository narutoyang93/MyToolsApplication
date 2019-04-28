package tools;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.naruto.mytoolsapplication.MyApplication;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2019/4/28 0028
 * @Note
 */
public class WeiXinHelper {
    // APP_ID
    private static final String APP_ID = "wxf367c307464dfef9";
    private static final int THUMB_SIZE = 150;

    public static IWXAPI getIWXAPI() {
        return Single.api;
    }

    private static void sendToWeiXin(Scene scene, String type, WXMediaMessage.IMediaObject mediaObject, String title, String description, Bitmap thumbBmp) {
        // 检查手机或者模拟器是否安装了微信
        if (!Single.api.isWXAppInstalled()) {
            Toast.makeText(MyApplication.getContext(), "您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }

        WXMediaMessage msg = new WXMediaMessage(mediaObject);
        msg.title = title;
        msg.description = description;
        if (thumbBmp!=null){
            //设置缩略图
            Bitmap thumbBmp0 = Bitmap.createScaledBitmap(thumbBmp, THUMB_SIZE, THUMB_SIZE, true);
            thumbBmp.recycle();
            msg.thumbData = Util.bmpToByteArray(thumbBmp0, true);
        }

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene.getValue();

        //调用api接口，发送数据到微信
        Single.api.sendReq(req);
    }

    /**
     * 分享文本
     *
     * @param text  文本内容
     * @param scene 分享到哪个场景
     */
    public static void sendText(String text, Scene scene) {
        sendToWeiXin(scene, "text", new WXTextObject(text), null, text, null);
    }

    /**
     * 分享图片
     *
     * @param image
     * @param scene
     */
    public static void sendImage(Bitmap image, Scene scene) {
        sendToWeiXin(scene, "img", new WXImageObject(image), null, null, image);
    }

    /**
     * 分享网页
     *
     * @param url
     * @param thumbBmp
     * @param scene
     * @param title
     * @param description
     */
    public static void sendWebPage(String url, Bitmap thumbBmp, Scene scene, String title, String description) {
        sendToWeiXin(scene, "webpage", new WXWebpageObject(url), title, description, thumbBmp);
    }


    private static String buildTransaction(String type) {
        return (type == null ? "" : type) + System.currentTimeMillis();
    }

    public static enum Scene {
        FRIEND(SendMessageToWX.Req.WXSceneSession),//好友
        FRIENDS_CIRCLE(SendMessageToWX.Req.WXSceneTimeline),//朋友圈
        COLLECTION(SendMessageToWX.Req.WXSceneFavorite);//收藏
        private int value;

        Scene(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 内部类方式实现线程安全单例模式
     */
    private static class Single {
        // IWXAPI 是第三方app和微信通信的openApi接口
        private static final IWXAPI api = regToWx();

        private static IWXAPI regToWx() {
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            IWXAPI api0 = WXAPIFactory.createWXAPI(MyApplication.getContext(), APP_ID, true);
            // 将应用的appId注册到微信
            api0.registerApp(APP_ID);
            return api0;
        }
    }
}
