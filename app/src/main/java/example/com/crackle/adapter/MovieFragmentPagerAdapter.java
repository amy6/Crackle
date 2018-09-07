package example.com.crackle.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import example.com.crackle.fragment.MovieCastFragment;
import example.com.crackle.fragment.MovieInfoFragment;
import example.com.crackle.fragment.MovieReviewsFragment;
import example.com.crackle.model.Movie;

import static example.com.crackle.utils.Constants.TAB_CAST;
import static example.com.crackle.utils.Constants.TAB_INFO;
import static example.com.crackle.utils.Constants.TAB_REVIEWS;

public class MovieFragmentPagerAdapter extends FragmentPagerAdapter {

    //view pager tab titles
    private static final String[] TAB_TITLES = {TAB_INFO, TAB_CAST, TAB_REVIEWS};
    private Movie movie;

    public MovieFragmentPagerAdapter(FragmentManager fm, Movie movie) {
        super(fm);
        this.movie = movie;
    }

    /**
     * called by the ViewPager to get the item type for the current position
     * @param position position of the fragment in ViewPager
     * @return fragment instance
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieInfoFragment.newInstance(movie);
            case 1:
                return MovieCastFragment.newInstance(movie);
            case 2:
                return MovieReviewsFragment.newInstance(movie);
        }
        return null;
    }

    /**
     * called by the ViewPager to know the number of fragments to be displayed
     * @return number of ViewPager fragments
     */
    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    /**
     * called by the ViewPager to get the title for each tab
     * @param position position of the tab
     * @return title for the tab
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
