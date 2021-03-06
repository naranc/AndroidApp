package com.narancommunity.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.narancommunity.app.common.DBHelper;
import com.narancommunity.app.common.Toaster;
import com.narancommunity.app.entity.AddressEntity;
import com.narancommunity.app.entity.UserInfo;
import com.narancommunity.app.net.AppConstants;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

//import com.umeng.commonsdk.UMConfigure;

/**
 * Writer：fancy on 2018/1/4 14:17
 * Email：120760202@qq.com
 * FileName :
 */

public class MApplication extends Application {
    public static int widthPixels = 720;// 屏幕宽度
    public static int heightPixels = 1280;// 屏幕高度
    private static MApplication instance;
    public static boolean isRelease = false;//是否为发布版
    public static boolean isTest = true;//是否为测试

    public synchronized static MApplication getInstance() {
        return instance;
    }

    public static final String TAG = MApplication.class.getSimpleName();

    static List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaranCom/");
        if (!folder.exists()) { //如果该文件夹不存在，则进行创建
            folder.mkdirs();//创建文件夹
        }
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.init(this, "5b067407b27b0a0a3700007d"
                , "tencent", UMConfigure.DEVICE_TYPE_PHONE, "");////58edcfeb310c93091c000be2 5965ee00734be40b580001a0

        PlatformConfig.setWeixin("wx742e4cb8ecc2ab6a", "3cb26f1a6f6454e4553d7df0a0f7168b");
        PlatformConfig.setSinaWeibo("448717741", "43de180aa4b10c7829786f77858d8678", "http://47.98.218.205:8082");
        PlatformConfig.setQQZone("100424468", "KEYzWVR9DNIKBGozMn7");

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("fancy", "lifecycle>>onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("fancy", "lifecycle>>onTerminate");
    }

    public static void putActivity(String key, Activity activity) {
        activityList.add(activity);
    }

    public static void finishAllActivity() {
        if (activityList != null) {
            if (activityList != null) {
                for (Activity activity : activityList) {
                    activity.finish();
                }
                activityList.clear();
            }
        }
    }

    public static void finishAllExceptMainActivity() {
        if (activityList != null) {
            if (activityList != null) {
                for (Activity activity : activityList) {
                    if (activity instanceof MainActivity) {
                        //不删除Main
                    } else
                        activity.finish();
                }
                activityList.clear();
            }
        }
    }

    /**
     * 获取用户的基本信息
     *
     * @return first-name ,
     * second-token
     */
    public static Pair<String, String> getUser() {
        UserInfo info = new UserInfo();
        try {
            DB snappyDb =
                    DBHelper.getDB(instance);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            if (isExists)
                info = snappyDb.get(AppConstants.USER_INFO, UserInfo.class);
            else {
                info.setPhone("");
                info.setAccessToken("");
            }
//            String phone = info.getUserPhone();
//            info.setUserPhone(Utils.checkUserNameValue(phone));
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return new Pair<>(info.getPhone(), info.getAccessToken());
    }

    /**
     * 获取用户的ID
     * <p>
     * second-token
     */
    public static int getAccountId(Context context) {
        UserInfo info = new UserInfo();
        try {
            DB snappyDb =
                    DBHelper.getDB(context);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            if (isExists)
                info = snappyDb.get(AppConstants.USER_INFO, UserInfo.class);
            else {
                info.setAccountId(0);
            }
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return info.getAccountId();
    }

    static Context getContext() {
        return MApplication.getContext();
    }

    /**
     * 获取用户的基本信息
     *
     * @return first-name ,
     * second-token
     */
    public static String getAccessToken() {
        UserInfo info = new UserInfo();
        try {
            DB snappyDb =
                    DBHelper.getDB(instance);
            synchronized (snappyDb) {
                boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
                if (isExists)
                    info = snappyDb.get(AppConstants.USER_INFO, UserInfo.class);
                else {
                    info.setAccessToken("");
                }
            }
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return info.getAccessToken();
    }

    /**
     * 获取用户的基本信息
     *
     * @return first-name ,
     * second-token
     */
    public static String getAccessToken(Context context) {
        UserInfo info = new UserInfo();
        try {
            DB snappyDb =
                    DBHelper.getDB(context);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            if (isExists)
                info = snappyDb.get(AppConstants.USER_INFO, UserInfo.class);
            else {
                info.setAccessToken("");
            }
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return info.getAccessToken();
    }


    /**
     * 获取用户的基本信息
     *
     * @return first-name ,
     * second-token
     */
    public static UserInfo getUserInfo(Context context) {
        UserInfo info = null;
        try {
            DB snappyDb =
                    DBHelper.getDB(context);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            if (isExists)
                info = snappyDb.get(AppConstants.USER_INFO, UserInfo.class);
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
            Log.e("fancy", e.getMessage());
        }
        return info;
    }

    /**
     * 获取用户的基本信息
     */
    public static void setUserInfo(UserInfo userInfo) {
        try {
            DB snappyDb =
                    DBHelper.getDB(instance);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            String token = "";
            if (isExists) {
                token = snappyDb.get(AppConstants.USER_INFO, UserInfo.class).getAccessToken();
            }
            userInfo.setAccessToken(token);
            snappyDb.put(AppConstants.USER_INFO, userInfo);
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAuthorisedSuccess(Context context) {
        boolean isAuthorised = false;
        try {
            DB snappyDb =
                    DBHelper.getDB(instance);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            if (isExists) {
                String state = snappyDb.get(AppConstants.USER_INFO, UserInfo.class).getCertificationType();
                if (state.equals("SUCCESS"))
                    isAuthorised = true;
            }
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return isAuthorised;
    }

    public static final String AUTH_INITIAL ="INITIAL";
    public static final String AUTH_GOING ="GOING";
    public static final String AUTH_SUCCESS ="SUCCESS";
    public static final String AUTH_FAIL ="FAIL";

    /**
     * 获取认证状态
     * @param context
     * @return INITIAL - 未进行过认证  GOING  - 正在进行认证中  SUCCESS - 成功了  FAIL   -  认证失败，需要重新提交认证
     */
    public static String getAuthorisedState(Context context) {
        String state = "";
        try {
            DB snappyDb =
                    DBHelper.getDB(instance);
            boolean isExists = snappyDb.exists(AppConstants.USER_INFO);
            if (isExists) {
                state = snappyDb.get(AppConstants.USER_INFO, UserInfo.class).getCertificationType();
            } else state = "INITIAL";
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        return state;
    }

    public static void logout(boolean isNeedReLogin) {
        try {
            DB snappyDb =
                    DBHelper.getDB(instance);
            snappyDb.del(AppConstants.USER_INFO);
            snappyDb.close();
            if (isNeedReLogin)
                Toaster.toast(instance, "账户过期，请重新登录！");
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static AddressEntity getAddress() {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity("杭州");
        addressEntity.setCounty("余杭区");
        addressEntity.setProvince("浙江省");
        addressEntity.setMailAddress("梦想小镇天使村");
        return addressEntity;
    }
}
