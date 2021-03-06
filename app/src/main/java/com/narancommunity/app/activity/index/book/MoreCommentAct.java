package com.narancommunity.app.activity.index.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.narancommunity.app.MApplication;
import com.narancommunity.app.MeItemInterface;
import com.narancommunity.app.R;
import com.narancommunity.app.activity.general.BaseActivity;
import com.narancommunity.app.adapter.CommentAdapter;
import com.narancommunity.app.common.CenteredToolbar;
import com.narancommunity.app.common.LoadDialog;
import com.narancommunity.app.common.Toaster;
import com.narancommunity.app.common.Utils;
import com.narancommunity.app.entity.CommentEntity;
import com.narancommunity.app.entity.CommentListEntity;
import com.narancommunity.app.interfaces.CommentInterfaces;
import com.narancommunity.app.net.NRClient;
import com.narancommunity.app.net.Result;
import com.narancommunity.app.net.ResultCallback;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Writer：fancy on 2018/1/16 17:09
 * Email：120760202@qq.com
 * FileName : 查看更多评论
 */

public class MoreCommentAct extends BaseActivity {
    @BindView(R.id.toolbar)
    CenteredToolbar toolbar;
    @BindView(R.id.recyclerView)
    XRecyclerView recyclerView;
//    @BindView(R.id.swipe_refresh)
//    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    CommentAdapter adapter;
    List<CommentEntity> list = new ArrayList<>();
    Integer bookId = 0;
    int pageSize = 5;
    int pageNum = 1;
    private int TOTAL_PAGE = 1;
    int tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_normal_list_view_with_swipe_refresh);
        ButterKnife.bind(this);
        toolbar.setTitle("更多评论");

        bookId = getIntent().getIntExtra("bookId", 0);
        tag = getIntent().getIntExtra("tag", 0);
        setBar(toolbar);

        setView();
        if (tag == 0) {
            getData();
        } else if (tag == 1) {
            getEssayData();
        }
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

    private void getData() {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", bookId);
        map.put("pageSize", pageSize);
        map.put("pageNum", pageNum);
        map.put("accessToken", MApplication.getAccessToken());
        NRClient.getCommentList(map, new ResultCallback<Result<CommentListEntity>>() {
            @Override
            public void onSuccess(Result<CommentListEntity> result) {
                LoadDialog.dismiss(getContext());
                setData(result.getData());
            }

            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
                recyclerView.loadMoreComplete();
                recyclerView.refreshComplete();
            }
        });
    }

    private void getEssayData() {
        Map<String, Object> map = new HashMap<>();
        map.put("contentId", bookId);
        map.put("pageSize", pageSize);
        map.put("pageNum", pageNum);
        map.put("accessToken", MApplication.getAccessToken());
        NRClient.getEssayCommentList(map, new ResultCallback<Result<CommentListEntity>>() {
            @Override
            public void onSuccess(Result<CommentListEntity> result) {
                LoadDialog.dismiss(getContext());
                setData(result.getData());
            }

            @Override
            public void onFailure(Throwable throwable) {
                LoadDialog.dismiss(getContext());
                Utils.showErrorToast(getContext(), throwable);
                recyclerView.loadMoreComplete();
                recyclerView.refreshComplete();
            }
        });
    }

    private void setData(CommentListEntity data) {
        if (pageNum == 1)
            list.clear();
        TOTAL_PAGE = data.getTotalPageNum();
        if (data != null && data.getComments() != null && data.getComments().size() > 0) {
            list.addAll(data.getComments());
            pageNum++;
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        recyclerView.loadMoreComplete();
        recyclerView.refreshComplete();
    }


    private void setView() {
        adapter = new CommentAdapter(getContext(), list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter.setListener(new CommentInterfaces() {
            @Override
            public void OnItemClick(int position) {
                startActivity(new Intent(getContext(), AddBookCommentAct.class)
                        .putExtra("tag", 1).putExtra("bookId", bookId)
                        .putExtra("commentedId", list.get(position).getCommentId())
                        .putExtra("replyName", list.get(position).getInitiatorNike()));
            }

            @Override
            public void OnAnswer(int id, String name) {
                startActivity(new Intent(getContext(), AddBookCommentAct.class)
                        .putExtra("tag", 1).putExtra("bookId", bookId)
                        .putExtra("commentedId", id)
                        .putExtra("replyName", name));
            }
        });
        adapter.setClickLike(new MeItemInterface() {
            @Override
            public void OnItemClick(int position) {
                clickLike(position);
            }

            @Override
            public void OnDelClick(int position) {

            }
        }, bookId);
        recyclerView.setAdapter(adapter);

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                if (tag == 0) {
                    getData();
                } else if (tag == 1) {
                    getEssayData();
                }
            }

            @Override
            public void onLoadMore() {
                if (pageNum <= TOTAL_PAGE) {
                    if (tag == 0) {
                        getData();
                    } else if (tag == 1) {
                        getEssayData();
                    }
                } else {
                    recyclerView.loadMoreComplete();
                    recyclerView.refreshComplete();
                    Toaster.toast(getContext(), "已无更多数据");
                }
            }
        });
    }

    private void clickLike(int position) {
        //TODO 点赞
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recyclerView != null) {
            recyclerView.destroy(); // this will totally release XR's memory
            recyclerView = null;
        }
    }
}
