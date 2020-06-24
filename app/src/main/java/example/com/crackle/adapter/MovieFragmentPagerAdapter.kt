package example.com.crackle.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import example.com.crackle.fragment.MovieCastFragment
import example.com.crackle.fragment.MovieInfoFragment
import example.com.crackle.fragment.MovieReviewsFragment
import example.com.crackle.model.Movie
import example.com.crackle.utils.Constants

class MovieFragmentPagerAdapter(fm: FragmentManager, private val movie: Movie) : FragmentPagerAdapter(fm) {

    /**
     * called by the ViewPager to get the item type for the current position
     * @param position position of the fragment in ViewPager
     * @return fragment instance
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MovieInfoFragment.newInstance(movie)
            1 -> MovieCastFragment.newInstance(movie)
            else -> MovieReviewsFragment.newInstance(movie)
        }
    }

    /**
     * called by the ViewPager to know the number of fragments to be displayed
     * @return number of ViewPager fragments
     */
    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    /**
     * called by the ViewPager to get the title for each tab
     * @param position position of the tab
     * @return title for the tab
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    companion object {

        //view pager tab titles
        private val TAB_TITLES = arrayOf(Constants.TAB_INFO, Constants.TAB_CAST, Constants.TAB_REVIEWS)
    }
}
