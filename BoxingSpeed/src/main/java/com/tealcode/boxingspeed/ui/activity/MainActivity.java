package com.tealcode.boxingspeed.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.ui.fragment.ArenaFragment;
import com.tealcode.boxingspeed.ui.fragment.BoxingFragment;
import com.tealcode.boxingspeed.ui.fragment.FriendsFragment;
import com.tealcode.boxingspeed.ui.fragment.ProfilerFragment;
import com.tealcode.boxingspeed.ui.fragment.RankingFragment;
import com.tealcode.boxingspeed.ui.widget.NaviTabButton;

/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class MainActivity extends FragmentActivity {
    private Fragment[] mFragments;
    private NaviTabButton[] mTabButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initTabButtons();

        initFragments();

        setFragmentIndicator(0);
    }

    private void initTabButtons()
    {
        mTabButtons = new NaviTabButton[5];

        mTabButtons[0] = (NaviTabButton) findViewById(R.id.tabbutton_boxing);
        mTabButtons[1] = (NaviTabButton) findViewById(R.id.tabbutton_friends);
        mTabButtons[2] = (NaviTabButton) findViewById(R.id.tabbutton_ranking);
        mTabButtons[3] = (NaviTabButton) findViewById(R.id.tabbutton_arena);
        mTabButtons[4] = (NaviTabButton) findViewById(R.id.tabbutton_profiler);

        mTabButtons[0].setTitle(getString(R.string.main_boxing));
        mTabButtons[0].setIndex(0);
        mTabButtons[0].setSelectedImage(ContextCompat.getDrawable(this, R.drawable.tab_boxing_sel));
        mTabButtons[0].setUnselectedImage(ContextCompat.getDrawable(this, R.drawable.tab_boxing_nosel));

        mTabButtons[1].setTitle(getString(R.string.main_friends));
        mTabButtons[1].setIndex(1);
        mTabButtons[1].setSelectedImage(ContextCompat.getDrawable(this, R.drawable.tab_friends_sel));
        mTabButtons[1].setUnselectedImage(ContextCompat.getDrawable(this, R.drawable.tab_friends_nosel));

        mTabButtons[2].setTitle(getString(R.string.main_ranking));
        mTabButtons[2].setIndex(2);
        mTabButtons[2].setSelectedImage(ContextCompat.getDrawable(this,R.drawable.tab_ranking_sel));
        mTabButtons[2].setUnselectedImage(ContextCompat.getDrawable(this,R.drawable.tab_ranking_nosel));

        mTabButtons[3].setTitle(getString(R.string.main_arena));
        mTabButtons[3].setIndex(3);
        mTabButtons[3].setSelectedImage(ContextCompat.getDrawable(this, R.drawable.tab_arena_sel));
        mTabButtons[3].setUnselectedImage(ContextCompat.getDrawable(this, R.drawable.tab_arena_nosel));

        mTabButtons[4].setTitle(getString(R.string.main_profiler));
        mTabButtons[4].setIndex(4);
        mTabButtons[4].setSelectedImage(ContextCompat.getDrawable(this, R.drawable.tab_profiler_sel));
        mTabButtons[4].setUnselectedImage(ContextCompat.getDrawable(this, R.drawable.tab_profiler_nosel));
    }

    private void initFragments()
    {
        mFragments = new Fragment[5];
        mFragments[0] = new BoxingFragment();
        mFragments[1] = new FriendsFragment();
        mFragments[2] = new RankingFragment();
        mFragments[3] = new ArenaFragment();
        mFragments[4] = new ProfilerFragment();
    }

    public void setFragmentIndicator(int which) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_fragment_container, mFragments[which]).commit();

//        getSupportFragmentManager().beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).show(mFragments[which]).commit();

        mTabButtons[0].setSelectedButton(false);
        mTabButtons[1].setSelectedButton(false);
        mTabButtons[2].setSelectedButton(false);
        mTabButtons[3].setSelectedButton(false);
        mTabButtons[4].setSelectedButton(false);

        mTabButtons[which].setSelectedButton(true);
    }
}
