package com.zyf.demo;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends Activity {
    private ButtonProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        requestContactSdPermission();
    }

    private void initView() {
        mProgressBar = (ButtonProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProgressBar.getStatus() == ButtonProgressBar.Status.Start) {
                    mProgressBar.setStatus(ButtonProgressBar.Status.End);
                } else {
                    OkGo.get("http://d2.apk8.com:8020/soft/dingdangkuaiyao.apk")//
                            .tag(this)//
                            .execute(new FileCallback() {  //文件下载时，可以指定下载的文件目录和文件名
                                @Override
                                public void onSuccess(File file, Call call, Response response) {
                                    // file 即为文件数据，文件保存在指定目录
                                    Log.e("oss", "下载成功");
                                    mProgressBar.setStatus(ButtonProgressBar.Status.End);
                                }

                                @Override
                                public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                                    //这里回调下载进度(该回调在主线程,可以直接更新ui)
                                    if (Math.round(progress * 100) < 100) {
                                        mProgressBar.setProgress(Math.round(progress * 100));
                                    } else {
                                        mProgressBar.setProgress(100);
                                    }
                                }

                                @Override
                                public void onBefore(BaseRequest request) {
                                    super.onBefore(request);
                                    mProgressBar.setStatus(ButtonProgressBar.Status.Start);
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 只需要调用这一句，剩下的AndPermission自动完成。
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void requestContactSdPermission() {
        AndPermission.with(this)
                .requestCode(102)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .send();
    }

    @PermissionYes(102)
    private void getYes() {
        Toast.makeText(this, "获取SD卡权限成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionNo(102)
    private void getNo() {
        Toast.makeText(this, "获取SD卡权限失败", Toast.LENGTH_SHORT).show();
    }


}
