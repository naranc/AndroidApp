package com.narancommunity.app.activity.general;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.narancommunity.app.MApplication;
import com.narancommunity.app.R;
import com.narancommunity.app.common.CenteredToolbar;
import com.narancommunity.app.common.DBHelper;
import com.narancommunity.app.common.ImageUtils;
import com.narancommunity.app.common.LoadDialog;
import com.narancommunity.app.common.SDCardUtils;
import com.narancommunity.app.common.Toaster;
import com.narancommunity.app.common.Utils;
import com.narancommunity.app.entity.UpdateFilesEntity;
import com.narancommunity.app.entity.UserInfo;
import com.narancommunity.app.net.AppConstants;
import com.narancommunity.app.net.NRClient;
import com.narancommunity.app.net.Result;
import com.narancommunity.app.net.ResultCallback;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Writer：fancy on 2018/1/4 14:57
 * Email：120760202@qq.com
 * FileName :
 */

public class AuthoriseSecondAct extends BaseActivity {

    @BindView(R.id.toolbar)
    CenteredToolbar toolbar;
    @BindView(R.id.iv_front)
    ImageView ivFront;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    Map<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_authorise_second);
        ButterKnife.bind(this);
        setBar(toolbar);

        toolbar.setTitle("上传身份证");
        map = (Map<String, Object>) getIntent().getSerializableExtra("data");
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {

            }
        }).start();

    }

    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog(final boolean isCamera) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ;
                    }
                });

        builder.setPositiveButton("去设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转至当前应用的权限设置页面
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    boolean isNowFront = true;

    @OnClick({R.id.iv_front, R.id.iv_back, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_front:
                isNowFront = true;
                checkCamera();
                break;
            case R.id.iv_back:
                isNowFront = false;
                checkCamera();
                break;
            case R.id.btn_submit:
                map.put("cardPositive", frontPic);
                map.put("cardOpposite", backPic);
                NRClient.authorise(map, new ResultCallback<Result<Void>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        LoadDialog.dismiss(getContext());
                        Utils.showErrorToast(getContext(), throwable);
                    }

                    @Override
                    public void onSuccess(Result<Void> result) {
                        LoadDialog.dismiss(getContext());
                        UserInfo info = MApplication.getUserInfo(getContext());
                        info.setCertificationType("GOING");
                        saveUserInfo(info);
                        Toaster.toastLong(getContext(), "提交成功，请耐心等待审核");
                        MApplication.finishAllExceptMainActivity();
                        finish();
                    }
                });
                break;
        }
    }

    /**
     * 保存用户信息
     *
     * @param data
     */
    private void saveUserInfo(UserInfo data) {
        try {
            DB snappyDb =
                    DBHelper.getDB(this);
            snappyDb.put(AppConstants.USER_INFO, data);
            snappyDb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    private static final int GET_IMAGE_VIA_CAMERA = 1001;

    private void checkCamera() {
        //第二个参数是需要申请的权限
        //权限已经被授予，在这里直接写要执行的相应方法即可
        if (checkCameraPermissions()) {
            //启动严格模式，执行老方法
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(SDCardUtils.getTempFile()));
            //TODO 详细的时候需要写
            startActivityForResult(intent, GET_IMAGE_VIA_CAMERA);
        } else {
            showMissingPermissionDialog(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case GET_IMAGE_VIA_SDCARD: // 相册
//                    List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                    List<File> listFiles = new ArrayList<>();
//                    for (int i = 0; i < mSelectPath.size(); i++) {
//                        File file = new File(mSelectPath.get(i));
//                        listFiles.add(file);
//                    }
//                    dealWithMapPic(listFiles);
//                    break;
                case GET_IMAGE_VIA_CAMERA: // 相机
                    // 拍完照片直接返回，不用截图
                    File picture = SDCardUtils.getTempFile();
                    dealWithCamPic(picture);
                    break;
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    boolean isUploaded = false;

    String frontPic = "", backPic = "";

    private void dealWithCamPic(final File picture) {
        LoadDialog.show(getContext(), "图片上传中，请稍候！");
        isUploaded = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double sizes = Utils.getFileOrFilesSize(picture.getAbsolutePath(), Utils.SIZETYPE_KB);
                String filename = System.currentTimeMillis() + "";
                File tempFile = null;
                if (sizes > AppConstants.IMAGE_SIZE_LIMITED) {
                    try {
                        ImageUtils.compressAndGenImage(picture.getAbsolutePath()
                                , SDCardUtils.getDefineCompressFile(filename).getAbsolutePath()
                                , AppConstants.IMAGE_SIZE_LIMITED
                                , false);
                        tempFile = SDCardUtils.getDefineCompressFile(filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    tempFile = picture;
                if (tempFile != null) {
                    Utils.setImgF(getContext(), tempFile, isNowFront ? ivFront : ivBack);
                    updateFiles(tempFile);
                }
                isUploaded = true;
            }
        }, 100);
    }

    private void updateFiles(final File file) {
        NRClient.uploadOneFile(file, new ResultCallback<Result<UpdateFilesEntity>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
            }

            @Override
            public void onSuccess(Result<UpdateFilesEntity> result) {
                LoadDialog.dismiss(getContext());
                if (result.getData() != null && result.getData().getData().size() > 0) {
                    Log.i("fancy", "----------");
                    if (result.getData().getData().size() > 0) {
                        String pic = result.getData().getData().get(0);
                        if (pic.contains("/")) {
                            String[] arr = pic.split("/");
                            pic = arr[arr.length - 1];
                        }
                        if (isNowFront) {
                            frontPic = pic;
                        } else
                            backPic = pic;
//                        deleteTempFile();
                    }
                } else {
                    Toaster.toast(getContext(), "数据为空！");
                }
            }
        });
    }


    private boolean checkCameraPermissions() {
        return AndPermission.hasPermissions(this, Manifest.permission.CAMERA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(getContext());
    }

//    RationaleListener rationaleListener = new RationaleListener() {
//        @Override
//        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
//            AlertDialog.newBuilder(getContext()).setTitle("温馨提示")
//                    .setMessage("您已拒绝了打开相机的权限，没办法进行实名认证了，是否打开？")
//                    .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            rationale.resume();
//                        }
//                    }).setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    rationale.cancel();
//                }
//            }).show();
//        }
//    };
}
