package cau.seoulargogaja;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WalletPageAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public WalletPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {            // 여행기간
            return new WalletPage1();
        } else if (position == 1) {
            return new WalletPage2(); // 예산
        } else
            return new WalletPage3(); // 총 지출

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.tab_page1);
        } else if (position == 1) {
            return mContext.getString(R.string.tab_page2);
        }  else
            return mContext.getString(R.string.tab_page3);
    }

    @Override
    public int getCount() {
        return 3; // 탭 3개
    }
}