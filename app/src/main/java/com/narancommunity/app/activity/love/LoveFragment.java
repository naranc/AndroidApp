package com.narancommunity.app.activity.love;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.narancommunity.app.MApplication;
import com.narancommunity.app.R;
import com.narancommunity.app.adapter.FindLatestAdapter;
import com.narancommunity.app.adapter.LoveAdapter;
import com.narancommunity.app.adapter.OnItemClickListener;
import com.narancommunity.app.common.LoadDialog;
import com.narancommunity.app.common.Toaster;
import com.narancommunity.app.common.Utils;
import com.narancommunity.app.entity.CompanyData;
import com.narancommunity.app.entity.CompanyEntity;
import com.narancommunity.app.entity.RecEntity;
import com.narancommunity.app.net.NRClient;
import com.narancommunity.app.net.Result;
import com.narancommunity.app.net.ResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Writer：fancy on 2017/12/19 14:41
 * Email：120760202@qq.com
 * FileName : 首页片段
 */

public class LoveFragment extends Fragment {
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.iv_add_love)
    ImageView ivAddLove;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.ln_top)
    LinearLayout top;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefreshLayout;
    Unbinder unbinder;

    LoveAdapter adapter;
    List<CompanyEntity> listData = new ArrayList<>();

    int pageSize = 6;
    int pageNum = 1;
    private int TOTAL_PAGE = 1;

    public static LoveFragment newInstance() {

        LoveFragment fragment = new LoveFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_love, container, false);
            unbinder = ButterKnife.bind(this, rootView);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) top.getLayoutParams();
                linearParams.height = Utils.dip2px(getContext(), 48);
                top.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                top.setPadding(0, 0, 0, 0);
            }
            setView();
            return rootView;
        } else {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            return rootView;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    private void setView() {
        final LinearLayoutManager lm_latest = new LinearLayoutManager(getContext());
        lm_latest.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new LoveAdapter(getContext());
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        recyclerView.setLayoutManager(lm_latest);
        recyclerView.setAdapter(adapter);
        adapter.setDataList(listData);
        adapter.notifyDataSetChanged();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageNum = 1;
                        getData();
                    }
                }, 1000);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItemPosition = lm_latest.findLastVisibleItemPosition();
                    Log.i("fancy", "最后的可见位置:" + lastVisibleItemPosition);
                    if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                        if (pageNum <= TOTAL_PAGE) {
                            getData();
                        } else
                            Toaster.toast(getContext(), "已无更多数据");
                    }
                }
            }
        });
    }

    private void getData() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        NRClient.getOrgList(map, new ResultCallback<Result<CompanyData>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(Result<CompanyData> result) {
                LoadDialog.dismiss(getContext());
                setDateView(result.getData());
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setDateView(CompanyData data) {
        if (data == null)
            return;
        if (pageNum == 1)
            listData.clear();
        TOTAL_PAGE = data.getTotalPageNum();
        if (data != null && data.getCompanys() != null && data.getCompanys().size() > 0) {
            listData.addAll(data.getCompanys());
            adapter.setDataList(listData);
            pageNum++;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("SplashScreen");
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("SplashScreen");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_add_love, R.id.iv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add_love:
                startActivity(new Intent(getContext(), SettleDownAct.class));
                break;
            case R.id.iv_search:
                break;
        }
    }
}
