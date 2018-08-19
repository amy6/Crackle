package example.com.crackle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MovieFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = {"INFO", "CAST", "REVIEWS"};

    public MovieFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieInfoFragment.newInstance();
            case 1:
                return MovieCastFragment.newInstance();
            case 2:
                return MovieReviewsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
