package example.com.crackle.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import example.com.crackle.R
import example.com.crackle.model.Image
import example.com.crackle.utils.Constants
import example.com.crackle.utils.Utils

class MovieImageAdapter(private val context: Context, private val imageUrl: List<Image>?) : PagerAdapter() {

    @JvmField
    @BindView(R.id.backdrop_image)
    internal var imageView: ImageView? = null

    /**
     * called by the adapter to instantiate individual viewpager view data
     *
     * @param container parent view group for the view to be inflated
     * @param position  position of the inflated view
     * @return reference to inflated view
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_movie_image, container, false)

        ButterKnife.bind(this, view)

        //set up url for backdrop images
        val backdropImageUrl = Constants.IMAGE_URL_SIZE + imageUrl!![position].filePath
        Glide.with(context)
                .setDefaultRequestOptions(Utils.setupGlide(Constants.BACKDROP_IMG))
                .load(backdropImageUrl)
                .into(imageView!!)

        //add inflated view to viewpager
        container.addView(view)

        return view
    }

    /**
     * called by the ViewPager to know the number of fragments to be displayed
     *
     * @return number of ViewPager fragments
     */
    override fun getCount(): Int {
        return imageUrl?.size ?: 0
    }

    /**
     * determines if the view is associated with the object (that is returned in instantiateitem method)
     *
     * @param view   reference to the view that needs to checked against the object
     * @param object reference to the key object that needs to be checked against the view
     * @return boolean indicating whether the view is associated with the key object
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    /**
     * removes a view from the given page
     *
     * @param container reference to the containing viewgroup that holds the view to be removed
     * @param position  position of the view to be removed
     * @param object    reference to the assoicated object returned in instantiateitem method
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
