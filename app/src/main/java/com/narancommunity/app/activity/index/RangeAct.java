package com.narancommunity.app.activity.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.narancommunity.app.R;
import com.narancommunity.app.activity.fragment.RangeDaRenFragmentNew;
import com.narancommunity.app.activity.general.BaseActivity;
import com.narancommunity.app.common.CenteredToolbar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Writer：fancy on 2017/12/27 11:39
 * Email：120760202@qq.com
 * FileName :
 */
public class RangeAct extends BaseActivity implements OnTabSelectListener {

    @BindView(R.id.toolbar)
    CenteredToolbar toolbar;
    @BindView(R.id.slide_tab)
    SlidingTabLayout slideTab;
    @BindView(R.id.vp)
    ViewPager vp;

    private String[] mTitles = {"日榜", "周榜", "月榜", "总榜"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    RangeDaRenFragmentNew firstFrag = RangeDaRenFragmentNew.newInstance();
    RangeDaRenFragmentNew secondFrag = RangeDaRenFragmentNew.newInstance();
    RangeDaRenFragmentNew thirdFrag = RangeDaRenFragmentNew.newInstance();
    RangeDaRenFragmentNew fourthFrag = RangeDaRenFragmentNew.newInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_range);
        ButterKnife.bind(this);

        setBar(toolbar);
        toolbar.setTitle("公益榜");

        firstFrag.setType(0);
        mFragments.add(firstFrag);
        secondFrag.setType(1);
        mFragments.add(secondFrag);
        thirdFrag.setType(2);
        mFragments.add(thirdFrag);
        fourthFrag.setType(3);
        mFragments.add(fourthFrag);

        Collections.addAll(mFragments);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        slideTab.setOnTabSelectListener(this);
        slideTab.setViewPager(vp);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
