package com.narancommunity.app.activity.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.narancommunity.app.activity.general.BaseActivity;
import com.narancommunity.app.R;
import com.narancommunity.app.common.CenteredToolbar;
import com.narancommunity.app.common.LoadDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Writer：fancy on 2018/1/24 16:33
 * Email：120760202@qq.com
 * FileName : 老版举报，不知道哪里要用的，现在用这个{@link ReportAct}
 */

public class JubaoAct extends BaseActivity {
    @BindView(R.id.toolbar)
    CenteredToolbar toolbar;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    Integer orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_jubao);
        ButterKnife.bind(this);
        toolbar.setTitle("举报");
        setBar(toolbar);
        orderId = getIntent().getIntExtra("orderId", 0);
    }

    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        LoadDialog.show(getContext(), "举报中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LoadDialog.dismiss(getContext());
            }
        }).start();
//        NRClient.feedback();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
