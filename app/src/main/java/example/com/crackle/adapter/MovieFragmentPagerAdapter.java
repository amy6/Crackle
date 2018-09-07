package example.com.crackle.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import example.com.crackle.fragment.MovieCastFragment;
import example.com.crackle.fragment.MovieInfoFragment;
import example.com.crackle.fragment.MovieReviewsFragment;
import example.com.crackle.model.Movie;

public class MovieFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = {"INFO", "CAST", "REVIEWS"};
    private Movie movie;

    public MovieFragmentPagerAdapter(FragmentManager fm, Movie movie) {
        super(fm);
        this.movie = movie;
    }

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
