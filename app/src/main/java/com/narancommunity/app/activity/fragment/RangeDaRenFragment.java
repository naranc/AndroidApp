package com.narancommunity.app.activity.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.narancommunity.app.MApplication;
import com.narancommunity.app.R;
import com.narancommunity.app.adapter.RangeListRangeAdapter;
import com.narancommunity.app.common.LoadDialog;
import com.narancommunity.app.common.Toaster;
import com.narancommunity.app.common.Utils;
import com.narancommunity.app.entity.GradeData;
import com.narancommunity.app.entity.RankEntity;
import com.narancommunity.app.net.NRClient;
import com.narancommunity.app.net.Result;
import com.narancommunity.app.net.ResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Writer：fancy on 2017/12/27 11:49
 * Email：120760202@qq.com
 * FileName : 公益达人(旧版)
 */

public class RangeDaRenFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rb_day)
    RadioButton rbDay;
    @BindView(R.id.tab_rdo_grp)
    RadioGroup tabRdoGrp;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.iv_level)
    ImageView ivLevel;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.iv_icon)
    SelectableRoundedImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.tv_grades)
    TextView tvGrades;
    @BindView(R.id.rb_star)
    RatingBar rbStar;
    @BindView(R.id.tv_times)
    TextView tvTimes;

    int pageSize = 10;
    int pageNum = 1;

//    List<RankEntity> listDay = new ArrayList<>();
//    List<RankEntity> listWeek = new ArrayList<>();
//    List<RankEntity> listMonth = new ArrayList<>();
//    List<RankEntity> listAll = new ArrayList<>();

    RangeListRangeAdapter adapter;
    List<RankEntity> listData = new ArrayList<>();//全部要传值给他
    private int TOTAL_PAGE = 1;

    public static RangeDaRenFragment newInstance() {

        RangeDaRenFragment fragment = new RangeDaRenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("BookSortSonFragment");
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("BookSortSonFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_range_daren, container, false);
            ButterKnife.bind(this, rootView);

            tabRdoGrp = rootView.findViewById(R.id.tab_rdo_grp);
            rbDay = rootView.findViewById(R.id.rb_day);
            rbDay.setChecked(true);
            tabRdoGrp.setOnCheckedChangeListener(mOnTabCheckedChangeListenernew);
            tabRdoGrp.setVisibility(View.VISIBLE);

            setListView();
            getDayData();
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

    private void getDayData() {
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", MApplication.getAccessToken());
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        NRClient.getDarenDayList(map, new ResultCallback<Result<GradeData>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
            }

            @Override
            public void onSuccess(Result<GradeData> result) {
                LoadDialog.dismiss(getContext());
                setData(result.getData());
            }
        });
    }

    private void getWeekData() {
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", MApplication.getAccessToken());
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        NRClient.getDarenWeekList(map, new ResultCallback<Result<GradeData>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
            }

            @Override
            public void onSuccess(Result<GradeData> result) {
                LoadDialog.dismiss(getContext());
                setData(result.getData());
            }
        });
    }

    private void getMonthData() {
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", MApplication.getAccessToken());
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        NRClient.getDarenMonthList(map, new ResultCallback<Result<GradeData>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
            }

            @Override
            public void onSuccess(Result<GradeData> result) {
                LoadDialog.dismiss(getContext());
                setData(result.getData());
            }
        });
    }

    private void getAllData() {
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", MApplication.getAccessToken());
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        NRClient.getDarenAllList(map, new ResultCallback<Result<GradeData>>() {
            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
            }

            @Override
            public void onSuccess(Result<GradeData> result) {
                LoadDialog.dismiss(getContext());
                setData(result.getData());
            }
        });
    }

    private void setData(GradeData data) {
        if (pageNum == 1) {
//            listDay.clear();
//            listWeek.clear();
//            listMonth.clear();
//            listAll.clear();
            listData.clear();
        }
        TOTAL_PAGE = data.getTotalPageNum();
        if (data != null && data.getRanks() != null && data.getRanks().size() > 0) {
            listData.addAll(data.getRanks());
//            if (currentPosition == 0) {
//                listDay.addAll(data.getRanks());
//                pageNum++;
//            } else if (currentPosition == 1) {
//                listWeek.addAll(data.getRanks());
//                pageNum++;
//            } else if (currentPosition == 2) {
//                listMonth.addAll(data.getRanks());
//                pageNum++;
//            } else if (currentPosition == 3) {
//                listAll.addAll(data.getRanks());
//                pageNum++;
//            }
            adapter.setList(listData);
            pageNum++;
        }
        adapter.notifyDataSetChanged();
        setMyData(data.getMyRank());
    }

    private void setMyData(RankEntity myRank) {
        int rankNum = Utils.getValue(myRank.getRankNum());
        if (rankNum > 2) {
            tvLevel.setText("" + rankNum);
            ivLevel.setVisibility(View.GONE);
            tvLevel.setVisibility(View.VISIBLE);
        } else {
            ivLevel.setVisibility(View.VISIBLE);
            tvLevel.setVisibility(View.GONE);
        }

        String url = Utils.getValue(myRank.getAccountImg());
        if (!"".equals(url))
            Utils.setImgF(getContext(), url, ivIcon);
        else Utils.setImgF(getContext(), R.mipmap.zw_morentouxiang, ivIcon);

        tvName.setText("" + Utils.getValue(myRank.getAccountName()));
        tvRemark.setText("" + Utils.getValue(myRank.getAccountRemark()));
        tvGrades.setVisibility(View.GONE);
        rbStar.setVisibility(View.GONE);
        tvTimes.setText("" + Utils.getValue(myRank.getRankScore()));

    }

    private void setListView() {
//        DividerItemDecoration dividerItemDecoration =
//                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);

        final LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayout);
        adapter = new RangeListRangeAdapter(getContext(), listData, 0);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItemPosition = linearLayout.findLastVisibleItemPosition();
                    Log.i("fancy", "最后的可见位置:" + lastVisibleItemPosition);
                    if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                        if (pageNum <= TOTAL_PAGE) {
                            if (currentPosition == 0) {
                                getDayData();
                            } else if (currentPosition == 1) {
                                getWeekData();
                            } else if (currentPosition == 2) {
                                getMonthData();
                            } else if (currentPosition == 3) {
                                getAllData();
                            }
                        } else {
                            //这个地方很恶心，不知道为什么没办法直接判断它是否为最后一条，往上拉也是最后一条，很离谱
                            View v = linearLayout.findViewByPosition(listData.size() - 1);
                            boolean is = !isViewCovered(v);
                            if (is)
                                Toaster.toast(getContext(), "已无更多数据");
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        pageNum = 1;
                        if (currentPosition == 0) {
                            getDayData();
                        } else if (currentPosition == 1) {
                            getWeekData();
                        } else if (currentPosition == 2) {
                            getMonthData();
                        } else if (currentPosition == 3) {
                            getAllData();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * Tab选择监听
     */
    private RadioGroup.OnCheckedChangeListener mOnTabCheckedChangeListenernew
            = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

            RadioButton checkedRdoBtn = radioGroup.findViewById(checkedId);
            checkedRdoBtn.playSoundEffect(SoundEffectConstants.CLICK);
            checkedRdoBtn.setChecked(true);
            int checkedPosition = radioGroup.indexOfChild(checkedRdoBtn);
            if (0 <= checkedPosition && checkedPosition < 4) {
                switchTo(checkedPosition);
            }
        }
    };

    int currentPosition = 0;

    private void switchTo(int checkedPosition) {
        pageNum = 1;
        currentPosition = checkedPosition;
        if (checkedPosition == 0)
            getDayData();
        else if (checkedPosition == 1)
            getWeekData();
        else if (checkedPosition == 2)
            getMonthData();
        else if (checkedPosition == 3)
            getAllData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public boolean isViewCovered(final View view) {
        View currentView = view;

        Rect currentViewRect = new Rect();
        boolean partVisible = currentView.getGlobalVisibleRect(currentViewRect);
        boolean totalHeightVisible = (currentViewRect.bottom - currentViewRect.top) >= view.getMeasuredHeight();
        boolean totalWidthVisible = (currentViewRect.right - currentViewRect.left) >= view.getMeasuredWidth();
        boolean totalViewVisible = partVisible && totalHeightVisible && totalWidthVisible;
        if (!totalViewVisible)//if any part of the view is clipped by any of its parents,return true
            return true;

        while (currentView.getParent() instanceof ViewGroup) {
            ViewGroup currentParent = (ViewGroup) currentView.getParent();
            if (currentParent.getVisibility() != View.VISIBLE)//if the parent of view is not visible,return true
                return true;

            int start = indexOfViewInParent(currentView, currentParent);
            for (int i = start + 1; i < currentParent.getChildCount(); i++) {
                Rect viewRect = new Rect();
                view.getGlobalVisibleRect(viewRect);
                View otherView = currentParent.getChildAt(i);
                Rect otherViewRect = new Rect();
                otherView.getGlobalVisibleRect(otherViewRect);
                if (Rect.intersects(viewRect, otherViewRect))//if view intersects its older brother(covered),return true
                    return true;
            }
            currentView = currentParent;
        }
        return false;
    }

    public int indexOfViewInParent(View view, ViewGroup parent) {
        int index;
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index) == view)
                break;
        }
        return index;
    }
}
